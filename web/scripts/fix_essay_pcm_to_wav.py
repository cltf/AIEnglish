#!/usr/bin/env python3
"""Wrap raw PCM (L16) files in a RIFF/WAVE header. Idempotent if already WAV."""

from __future__ import annotations

import argparse
import struct
import sys
from pathlib import Path


def build_wav_pcm16le(pcm: bytes, sample_rate: int = 24000, channels: int = 1) -> bytes:
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


def main() -> int:
    ap = argparse.ArgumentParser(description="Fix essay offline audio: PCM L16 -> WAV")
    ap.add_argument("--dir", default="web/data/audio/essays", help="Directory of .wav files")
    ap.add_argument("--rate", type=int, default=24000)
    args = ap.parse_args()
    root = Path(args.dir)
    if not root.is_dir():
        print("not a directory:", root, file=sys.stderr)
        return 1
    n_fix = 0
    n_skip = 0
    for p in sorted(root.glob("*.wav")):
        data = p.read_bytes()
        if len(data) >= 12 and data[:4] == b"RIFF" and data[8:12] == b"WAVE":
            n_skip += 1
            continue
        fixed = build_wav_pcm16le(data, sample_rate=args.rate)
        p.write_bytes(fixed)
        print("[fix]", p.name, "->", len(fixed), "bytes")
        n_fix += 1
    print(f"Done. fixed={n_fix} skipped_already_wav={n_skip}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
