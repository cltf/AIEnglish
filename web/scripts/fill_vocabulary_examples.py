#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从 dictionaryapi.dev 补充英文例句，并用 MyMemory 接口生成中文译文（写入 example 与 exampleZh）。
多词短语若整词无结果，会尝试取短语中第一个单词的例句。
运行：python3 web/scripts/fill_vocabulary_examples.py [最多条数]
需联网；每条词约 2 次请求，内置限速与断点保存。
"""

import json
import os
import random
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
VJSON = os.path.join(ROOT, "web", "data", "vocabulary.json")
VJS = os.path.join(ROOT, "web", "data", "vocabulary-data.js")

UA = "AIEnglish-ExampleBot/1.0 (educational; +https://github.com/)"
BASE = "https://api.dictionaryapi.dev/api/v2/entries/en/"
TRANSLATE = "https://api.mymemory.translated.net/get"


def extract_best_example(entry_list, word_key: str):
    if not entry_list or not isinstance(entry_list, list):
        return None
    candidates = []
    for entry in entry_list:
        for meaning in entry.get("meanings") or []:
            for d in meaning.get("definitions") or []:
                ex = d.get("example")
                if ex and isinstance(ex, str) and ex.strip():
                    candidates.append(ex.strip())
    if not candidates:
        return None
    boundary = re.compile(r"\b" + re.escape(word_key) + r"\b", re.I)
    for ex in candidates:
        if boundary.search(ex):
            return ex
    return candidates[0]


def fetch_json(url, retries=3):
    req = urllib.request.Request(url, headers={"User-Agent": UA})
    last_err = None
    for attempt in range(retries):
        try:
            with urllib.request.urlopen(req, timeout=20) as r:
                return json.loads(r.read().decode("utf-8"))
        except urllib.error.HTTPError as e:
            if e.code == 429:
                time.sleep(2 + attempt * 2)
                last_err = e
                continue
            if e.code == 404:
                return None
            last_err = e
            time.sleep(0.5 * (attempt + 1))
        except Exception as e:
            last_err = e
            time.sleep(0.5 * (attempt + 1))
    if last_err:
        sys.stderr.write(f"fetch failed {url}: {last_err}\n")
    return None


def example_for_word(word: str, _phrase_retry: bool = True):
    slug = urllib.parse.quote(word.strip(), safe="")
    if not slug:
        return None
    data = fetch_json(BASE + slug)
    ex = extract_best_example(data, word.strip())
    if ex:
        return ex
    parts = word.split()
    if _phrase_retry and len(parts) > 1:
        return example_for_word(parts[0], _phrase_retry=False)
    return None


def normalize_example(text: str) -> str:
    t = text.strip()
    if not t:
        return ""
    if t and t[-1] not in ".!?":
        t += "."
    return t


def translate_en_to_zh(text: str) -> str:
    t = text.strip()
    if not t:
        return ""
    q = urllib.parse.quote(t, safe="")
    url = f"{TRANSLATE}?q={q}&langpair=en|zh-CN"
    data = fetch_json(url)
    if not data:
        return ""
    zh = (data.get("responseData") or {}).get("translatedText") or ""
    return zh.strip() if isinstance(zh, str) else ""


def write_outputs(data):
    with open(VJSON, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=0)
    with open(VJS, "w", encoding="utf-8") as f:
        f.write("window.VOCABULARY_DATA = ")
        json.dump(data, f, ensure_ascii=False)
        f.write(";\n")


def main():
    if not os.path.isfile(VJSON):
        print("Missing", VJSON, file=sys.stderr)
        sys.exit(1)
    max_n = int(sys.argv[1]) if len(sys.argv) > 1 and sys.argv[1].isdigit() else None
    with open(VJSON, "r", encoding="utf-8") as f:
        data = json.load(f)
    words = data.get("words") or []
    filled = 0
    skipped = 0
    failed = 0
    n = len(words)
    if max_n is not None:
        n = min(n, max_n)
        print(f"Limit run: first {n} words only", flush=True)
    for i, rec in enumerate(words):
        if max_n is not None and i >= max_n:
            break
        w = rec.get("word") or ""
        existing_ex = (rec.get("example") or "").strip()
        existing_zh = (rec.get("exampleZh") or "").strip()

        if existing_ex and existing_zh:
            skipped += 1
        elif existing_ex and not existing_zh:
            zh = translate_en_to_zh(existing_ex)
            rec["exampleZh"] = zh or ""
            filled += 1
            time.sleep(0.08 + random.uniform(0, 0.06))
        else:
            ex = example_for_word(w)
            if ex:
                rec["example"] = normalize_example(ex)
                zh = translate_en_to_zh(rec["example"])
                rec["exampleZh"] = zh or ""
                filled += 1
                time.sleep(0.08 + random.uniform(0, 0.06))
            else:
                rec["example"] = ""
                rec["exampleZh"] = ""
                failed += 1

        if (i + 1) % 50 == 0:
            print(
                f"... {i + 1}/{n} (updated {filled}, complete_skip {skipped}, no_example {failed})",
                flush=True,
            )
            write_outputs(data)
        time.sleep(0.12 + random.uniform(0, 0.08))
    write_outputs(data)
    print(
        f"Done. Processed {n}, updated {filled}, already_complete {skipped}, no_example {failed}",
        flush=True,
    )


if __name__ == "__main__":
    main()
