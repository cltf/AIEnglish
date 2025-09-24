#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
找出dict文件中无法解析的62个特殊格式单词
"""

import re

def parse_line_format3(line):
    """
    解析格式3: 单词 - 中文意思
    例如: abandon - 抛弃，放弃
    """
    pattern = r'^([a-zA-Z\s\(\)\-]+)\s*-\s*(.*)'
    match = re.match(pattern, line.strip())
    
    if match:
        word, meaning = match.groups()
        word = word.strip()
        return word
    return None

def find_unparseable_words():
    """找出无法解析的单词"""
    unparseable_words = []
    
    with open('词库/dict', 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            
            word = parse_line_format3(line)
            if not word:
                unparseable_words.append((line_num, line))
    
    return unparseable_words

def categorize_unparseable_words(unparseable_words):
    """对无法解析的单词进行分类"""
    categories = {
        '包含括号和斜杠': [],
        '包含等号': [],
        '包含撇号': [],
        '包含省略号': [],
        '包含斜杠': [],
        '包含破折号': [],
        '包含复数标记': [],
        '包含动词变位': [],
        '包含比较级': [],
        '包含缩写': [],
        '包含其他特殊字符': []
    }
    
    for line_num, line in unparseable_words:
        if '(' in line and '/' in line:
            categories['包含括号和斜杠'].append((line_num, line))
        elif '=' in line:
            categories['包含等号'].append((line_num, line))
        elif "'" in line:
            categories['包含撇号'].append((line_num, line))
        elif '…' in line:
            categories['包含省略号'].append((line_num, line))
        elif '/' in line and '=' not in line:
            categories['包含斜杠'].append((line_num, line))
        elif '–' in line or '—' in line:
            categories['包含破折号'].append((line_num, line))
        elif '(复' in line or '复数' in line:
            categories['包含复数标记'].append((line_num, line))
        elif '比较级' in line or '最高级' in line:
            categories['包含比较级'].append((line_num, line))
        elif 'abbr.' in line or '=' in line:
            categories['包含缩写'].append((line_num, line))
        else:
            categories['包含其他特殊字符'].append((line_num, line))
    
    return categories

def main():
    """主函数"""
    print("查找dict文件中无法解析的62个特殊格式单词...")
    
    unparseable_words = find_unparseable_words()
    print(f"找到 {len(unparseable_words)} 个无法解析的单词")
    
    # 分类显示
    categories = categorize_unparseable_words(unparseable_words)
    
    print("\n=== 分类统计 ===")
    for category, words in categories.items():
        if words:
            print(f"{category}: {len(words)}个")
    
    print("\n=== 详细列表 ===")
    for category, words in categories.items():
        if words:
            print(f"\n【{category}】({len(words)}个):")
            print("-" * 60)
            for line_num, line in words:
                print(f"行 {line_num:4d}: {line}")
    
    print(f"\n=== 总结 ===")
    print(f"无法解析的单词总数: {len(unparseable_words)}")
    print(f"dict文件总行数: 1689")
    print(f"成功解析: {1689 - len(unparseable_words)}")
    print(f"解析成功率: {((1689 - len(unparseable_words)) / 1689 * 100):.1f}%")

if __name__ == "__main__":
    main()


