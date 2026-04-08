#!/usr/bin/env python3
"""Batch-generate essay WAV files via local proxy TTS endpoint.

Output layout:
  web/data/audio/essays/<sample-id>.wav
"""

from __future__ import annotations

import argparse
import io
import json
import pathlib
import re
import struct
import sys
import time
import urllib.error
import urllib.request
import wave


def essay_tts_source_text(body: str) -> str:
    """Strip translation / 要点 blocks so TTS only receives English (see essays.json markers)."""
    s = body.strip()
    marker = "【中文翻译】"
    if marker in s:
        s = s.split(marker, 1)[0].strip()
    if s.startswith("【英文范文】"):
        s = s[len("【英文范文】") :].strip()
    return s


_FOOTER_WORDCOUNT_RE = re.compile(r"\n*（全文约\d+字）\s*$")


def prepare_tts_text(body: str) -> str:
    """Text sent to TTS: essay body markers stripped + 字数 footer removed."""
    s = essay_tts_source_text(body)
    s = _FOOTER_WORDCOUNT_RE.sub("", s).strip()
    return s


def chunk_tts_text(text: str, max_chars: int = 180) -> list[str]:
    """Split long essays into short segments at sentence boundaries so TTS does not skip mid-text.

    Greedy-merge sentences (split on 。！？；) so each chunk stays under max_chars; joining all
    chunks reproduces the essay text with paragraph newlines removed (same spoken order as原文).
    """
    text = text.strip()
    if not text:
        return []
    if len(text) <= max_chars:
        return [text]

    segments: list[str] = []
    for para in re.split(r"\n\s*\n", text):
        para = para.strip()
        if not para:
            continue
        for part in re.split(r"(?<=[。！？；])", para):
            part = part.strip()
            if part:
                segments.append(part)
    if not segments:
        return [text[:max_chars]]

    chunks: list[str] = []
    buf = ""
    for seg in segments:
        if len(seg) > max_chars:
            if buf:
                chunks.append(buf)
                buf = ""
            for i in range(0, len(seg), max_chars):
                chunks.append(seg[i : i + max_chars])
            continue
        if len(buf) + len(seg) <= max_chars:
            buf = buf + seg if buf else seg
        else:
            if buf:
                chunks.append(buf)
            buf = seg
    if buf:
        chunks.append(buf)

    ref = "".join(segments)
    merged = "".join(chunks)
    if merged != ref:
        raise RuntimeError(
            f"chunk_tts_text internal error: merged len {len(merged)} != segments len {len(ref)}"
        )
    return chunks


def combine_wav_bytes(parts: list[bytes], silence_ms: float = 220) -> bytes:
    """Concatenate WAV/PCM chunks (same rate/channels), with brief silence between."""
    if not parts:
        return b""
    if len(parts) == 1:
        return pcm16_to_wav_if_needed(parts[0])

    pcm_pieces: list[bytes] = []
    rate = 24000
    channels = 1
    sampwidth = 2

    for i, raw in enumerate(parts):
        wbytes = pcm16_to_wav_if_needed(raw)
        with wave.open(io.BytesIO(wbytes), "rb") as w:
            rate = w.getframerate()
            channels = w.getnchannels()
            sampwidth = w.getsampwidth()
            frames = w.readframes(w.getnframes())
        if i > 0:
            n = int(rate * (silence_ms / 1000.0) * channels * sampwidth)
            pcm_pieces.append(b"\x00" * n)
        pcm_pieces.append(frames)

    pcm = b"".join(pcm_pieces)
    return pcm16_to_wav_if_needed(pcm, sample_rate=rate, channels=channels)


def pcm16_to_wav_if_needed(data: bytes, sample_rate: int = 24000, channels: int = 1) -> bytes:
    """Proxy TTS may return raw L16 PCM; Android MediaPlayer requires RIFF WAVE."""
    if len(data) >= 12 and data[:4] == b"RIFF" and data[8:12] == b"WAVE":
        return data
    pcm = data
    if len(pcm) % 2:
        pcm = pcm[:-1]
    bits = 16
    block_align = channels * bits // 8
    byte_rate = sample_rate * block_align
    data_size = len(pcm)
    chunk_size = 36 + data_size
    out = bytearray()
    out += b"RIFF" + struct.pack("<I", chunk_size) + b"WAVE"
    out += b"fmt " + struct.pack("<IHHIIHH", 16, 1, channels, sample_rate, byte_rate, block_align, bits)
    out += b"data" + struct.pack("<I", data_size)
    out += pcm
    return bytes(out)


def synthesize(endpoint: str, text: str, model: str, voice_name: str, timeout: int) -> bytes:
    payload = json.dumps(
        {"model": model, "voiceName": voice_name, "text": text},
        ensure_ascii=False,
    ).encode("utf-8")
    req = urllib.request.Request(
        endpoint,
        data=payload,
        method="POST",
        headers={"Content-Type": "application/json"},
    )
    with urllib.request.urlopen(req, timeout=timeout) as resp:
        data = resp.read()
        if not data:
            raise RuntimeError("TTS returned empty audio")
        return data


def synthesize_with_retries(
    endpoint: str,
    text: str,
    model: str,
    voice_name: str,
    timeout: int,
    max_retries: int = 5,
) -> bytes:
    """Retry on transient TTS / proxy errors (500, 502, 503, 429)."""
    for attempt in range(max_retries):
        try:
            return synthesize(endpoint, text, model, voice_name, timeout)
        except urllib.error.HTTPError as e:
            detail = e.read().decode("utf-8", errors="ignore")
            if e.code in (500, 502, 503, 429) and attempt < max_retries - 1:
                wait = 1.5 * (attempt + 1)
                print(f"  [retry {attempt + 1}/{max_retries}] HTTP {e.code}, sleep {wait:.1f}s")
                time.sleep(wait)
                continue
            raise RuntimeError(f"HTTP {e.code}: {detail[:500]}") from None
        except (TimeoutError, OSError, urllib.error.URLError) as e:
            if attempt < max_retries - 1:
                wait = 1.5 * (attempt + 1)
                print(f"  [retry {attempt + 1}/{max_retries}] {e!s}, sleep {wait:.1f}s")
                time.sleep(wait)
                continue
            raise
    raise RuntimeError("TTS retries exhausted")


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate offline essay audio files")
    parser.add_argument(
        "--essays-json",
        default="web/data/essays.json",
        help="Path to essays.json",
    )
    parser.add_argument(
        "--out-dir",
        default="web/data/audio/essays",
        help="Output directory for wav files",
    )
    parser.add_argument(
        "--endpoint",
        default="http://127.0.0.1:8787/openai-compatible/v1/essay-tts",
        help="TTS endpoint URL",
    )
    parser.add_argument("--model", default="gemini-2.5-flash-tts")
    parser.add_argument("--voice-name", default="Kore")
    parser.add_argument("--timeout", type=int, default=120)
    parser.add_argument("--force", action="store_true", help="Regenerate existing wav files")
    parser.add_argument(
        "--only",
        default="",
        help="Comma-separated sample ids (e.g. beijing-2025-s6,beijing-2025-s7); default: all",
    )
    args = parser.parse_args()

    essays_path = pathlib.Path(args.essays_json)
    out_dir = pathlib.Path(args.out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    payload = json.loads(essays_path.read_text(encoding="utf-8"))
    exams = payload.get("exams", [])

    only_ids = None
    if args.only.strip():
        only_ids = {x.strip() for x in args.only.split(",") if x.strip()}

    total = 0
    skipped = 0
    failed = 0
    for exam in exams:
        for sample in exam.get("samples", []):
            sample_id = str(sample["id"]).strip()
            if only_ids is not None and sample_id not in only_ids:
                continue
            total += 1
            body = str(sample["body"]).strip()
            tts_text = prepare_tts_text(body)
            wav_path = out_dir / f"{sample_id}.wav"
            if wav_path.exists() and not args.force:
                skipped += 1
                continue
            if not tts_text:
                failed += 1
                print(f"[FAIL] {sample_id}: empty text")
                continue
            try:
                chunk_max = 180
                if len(tts_text) <= chunk_max:
                    audio = synthesize_with_retries(
                        endpoint=args.endpoint,
                        text=tts_text,
                        model=args.model,
                        voice_name=args.voice_name,
                        timeout=args.timeout,
                    )
                    out_bytes = pcm16_to_wav_if_needed(audio)
                else:
                    segs = chunk_tts_text(tts_text, max_chars=chunk_max)
                    print(f"  [chunk] {sample_id}: {len(segs)} segments (long text)")
                    wav_parts: list[bytes] = []
                    for seg in segs:
                        wav_parts.append(
                            synthesize_with_retries(
                                endpoint=args.endpoint,
                                text=seg,
                                model=args.model,
                                voice_name=args.voice_name,
                                timeout=args.timeout,
                            )
                        )
                        time.sleep(0.2)
                    out_bytes = combine_wav_bytes(wav_parts)
                wav_path.write_bytes(out_bytes)
                print(f"[OK] {sample_id} -> {wav_path}")
            except Exception as e:  # pylint: disable=broad-except
                failed += 1
                print(f"[FAIL] {sample_id}: {e}")
            time.sleep(0.15)

    generated = total - skipped - failed
    print(
        f"\nDone. total={total}, generated={generated}, skipped={skipped}, failed={failed}, out={out_dir}"
    )
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
