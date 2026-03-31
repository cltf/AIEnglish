#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
根据 Android `ic_launcher` 资源（背景 #3DDC84 + foreground 矢量）生成 iOS AppIcon。
"""
from __future__ import annotations

import json
import shutil
from pathlib import Path

from PIL import Image, ImageDraw

BG = (61, 220, 132, 255)
FG_GREEN = (61, 220, 132, 255)
WHITE = (255, 255, 255, 255)


def render_master(size: int = 1024) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    s = size / 108.0
    cx, cy = 54 * s, 54 * s
    r48, r24, r12 = 48 * s, 24 * s, 12 * s
    draw.rectangle([0, 0, size - 1, size - 1], fill=BG)
    draw.ellipse([cx - r48, cy - r48, cx + r48, cy + r48], fill=WHITE)
    draw.ellipse([cx - r24, cy - r24, cx + r24, cy + r24], fill=FG_GREEN)
    cy2 = 66 * s
    draw.ellipse([cx - r12, cy2 - r12, cx + r12, cy2 + r12], fill=FG_GREEN)
    return img


def main() -> None:
    root = Path(__file__).resolve().parent.parent
    out_dir = root / "AIEnglish" / "Assets.xcassets" / "AppIcon.appiconset"
    out_dir.mkdir(parents=True, exist_ok=True)

    master = render_master(1024)

    def save(name: str, dim: int) -> None:
        im = master.resize((dim, dim), Image.Resampling.LANCZOS)
        im.save(out_dir / name, "PNG")

    save("Icon-20@1x.png", 20)
    save("Icon-20@2x.png", 40)
    save("Icon-20@3x.png", 60)
    save("Icon-29@1x.png", 29)
    save("Icon-29@2x.png", 58)
    save("Icon-29@3x.png", 87)
    save("Icon-40@1x.png", 40)
    save("Icon-40@2x.png", 80)
    save("Icon-40@3x.png", 120)
    shutil.copyfile(out_dir / "Icon-40@3x.png", out_dir / "Icon-60@2x.png")
    save("Icon-60@3x.png", 180)
    save("Icon-76@1x.png", 76)
    save("Icon-76@2x.png", 152)
    save("Icon-83.5@2x.png", 167)
    save("Icon-1024.png", 1024)

    contents = {
        "images": [
            {"filename": "Icon-20@2x.png", "idiom": "iphone", "scale": "2x", "size": "20x20"},
            {"filename": "Icon-20@3x.png", "idiom": "iphone", "scale": "3x", "size": "20x20"},
            {"filename": "Icon-29@2x.png", "idiom": "iphone", "scale": "2x", "size": "29x29"},
            {"filename": "Icon-29@3x.png", "idiom": "iphone", "scale": "3x", "size": "29x29"},
            {"filename": "Icon-40@2x.png", "idiom": "iphone", "scale": "2x", "size": "40x40"},
            {"filename": "Icon-40@3x.png", "idiom": "iphone", "scale": "3x", "size": "40x40"},
            {"filename": "Icon-60@2x.png", "idiom": "iphone", "scale": "2x", "size": "60x60"},
            {"filename": "Icon-60@3x.png", "idiom": "iphone", "scale": "3x", "size": "60x60"},
            {"filename": "Icon-20@1x.png", "idiom": "ipad", "scale": "1x", "size": "20x20"},
            {"filename": "Icon-20@2x.png", "idiom": "ipad", "scale": "2x", "size": "20x20"},
            {"filename": "Icon-29@1x.png", "idiom": "ipad", "scale": "1x", "size": "29x29"},
            {"filename": "Icon-29@2x.png", "idiom": "ipad", "scale": "2x", "size": "29x29"},
            {"filename": "Icon-40@1x.png", "idiom": "ipad", "scale": "1x", "size": "40x40"},
            {"filename": "Icon-40@2x.png", "idiom": "ipad", "scale": "2x", "size": "40x40"},
            {"filename": "Icon-76@1x.png", "idiom": "ipad", "scale": "1x", "size": "76x76"},
            {"filename": "Icon-76@2x.png", "idiom": "ipad", "scale": "2x", "size": "76x76"},
            {"filename": "Icon-83.5@2x.png", "idiom": "ipad", "scale": "2x", "size": "83.5x83.5"},
            {"filename": "Icon-1024.png", "idiom": "ios-marketing", "scale": "1x", "size": "1024x1024"},
        ],
        "info": {"author": "xcode", "version": 1},
    }
    (out_dir / "Contents.json").write_text(
        json.dumps(contents, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
    )
    print("OK:", out_dir)


if __name__ == "__main__":
    main()
