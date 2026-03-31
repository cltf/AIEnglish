#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""从项目根目录 process_vocabulary.py 中的 vocabulary_data 生成 web/data/vocabulary.json 与 vocabulary-data.js"""

import json
import os
import re
import sys

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
PROCESS_FILE = os.path.join(ROOT, "process_vocabulary.py")
OUT_JSON = os.path.join(ROOT, "web", "data", "vocabulary.json")
OUT_JS = os.path.join(ROOT, "web", "data", "vocabulary-data.js")


def extract_vocabulary_block():
    with open(PROCESS_FILE, "r", encoding="utf-8") as f:
        text = f.read()
    start = text.find('vocabulary_data = """')
    if start == -1:
        raise SystemExit("vocabulary_data block not found")
    start += len('vocabulary_data = """')
    end = text.find('"""', start)
    if end == -1:
        raise SystemExit("closing quote not found")
    return text[start:end]


def canonical_word(word_raw: str) -> str:
    w = word_raw.strip()
    w = w.rstrip(")").strip()
    if "(" in w:
        w = w.split("(")[0].strip()
    w = re.sub(r"\s*=\s*[^=].*$", "", w).strip()
    return w.lower()


def merge_duplicate_records(records: list) -> list:
    """合并同 key 词条的多条义项（如 may / May、Miss / miss）。"""
    by_key: dict = {}
    order = []
    for rec in records:
        k = rec["word"]
        if k not in by_key:
            by_key[k] = rec
            order.append(k)
            continue
        existing = by_key[k]
        defs = existing["definitions"] + rec["definitions"]
        seen_m = set()
        merged = []
        for d in defs:
            m = d.get("meaning", "")
            if m and m not in seen_m:
                seen_m.add(m)
                merged.append(d)
        existing["definitions"] = merged
    return [by_key[k] for k in order]


def parse_line(line: str):
    line = line.strip()
    if not line:
        return None
    m = re.match(r"^(\d+)\.\s*(.+)$", line)
    if not m:
        return None
    body = m.group(2)
    idx = body.find(" - ")
    if idx == -1:
        return None
    word_raw = body[:idx].strip()
    gloss = body[idx + 3 :].strip()
    if not word_raw or not gloss:
        return None
    return word_raw, gloss


def build_records(block: str):
    records = []
    for line in block.splitlines():
        p = parse_line(line)
        if not p:
            continue
        word_raw, gloss = p
        key = canonical_word(word_raw)
        if not key:
            continue
        records.append(
            {
                "word": key,
                "phonetic": "",
                "type": "MIDDLE_SCHOOL",
                "definitions": [{"partOfSpeech": "", "meaning": gloss}],
                "example": "",
                "exampleZh": "",
            }
        )
    return merge_duplicate_records(records)


def main():
    if not os.path.isfile(PROCESS_FILE):
        print("Missing:", PROCESS_FILE, file=sys.stderr)
        sys.exit(1)
    block = extract_vocabulary_block()
    words = build_records(block)
    out = {"version": 1, "words": words}
    os.makedirs(os.path.dirname(OUT_JSON), exist_ok=True)
    with open(OUT_JSON, "w", encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, indent=0)
    with open(OUT_JS, "w", encoding="utf-8") as f:
        f.write("window.VOCABULARY_DATA = ")
        json.dump(out, f, ensure_ascii=False)
        f.write(";\n")
    print(f"Wrote {len(words)} words to {OUT_JSON} and {OUT_JS}")


if __name__ == "__main__":
    main()
