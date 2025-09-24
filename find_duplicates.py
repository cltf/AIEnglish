#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
找出dict文件中重复的单词
"""

import re
from collections import defaultdict

def parse_line_format3(line):
    """
    解析格式3: 单词 - 中文意思
    例如: abandon - 抛弃，放弃
    """
    pattern = r'^([a-zA-Z\s\(\)]+)\s*-\s*(.*)'
    match = re.match(pattern, line.strip())
    
    if match:
        word, meaning = match.groups()
        # 清理单词
        word = word.strip()
        return word
    return None

def find_duplicates():
    """找出dict文件中的重复单词"""
    word_count = defaultdict(list)
    
    with open('词库/dict', 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            
            word = parse_line_format3(line)
            if word:
                word_count[word].append((line_num, line))
    
    # 找出重复的单词
    duplicates = {word: lines for word, lines in word_count.items() if len(lines) > 1}
    
    return duplicates

def main():
    """主函数"""
    print("查找dict文件中的重复单词...")
    
    duplicates = find_duplicates()
    
    print(f"找到 {len(duplicates)} 个重复的单词:")
    print("-" * 80)
    
    total_duplicate_lines = 0
    for word, lines in duplicates.items():
        print(f"\n单词: {word}")
        print(f"出现次数: {len(lines)}")
        for line_num, line in lines:
            print(f"  行 {line_num:4d}: {line}")
        total_duplicate_lines += len(lines) - 1  # 减去1，因为只计算重复的部分
    
    print("-" * 80)
    print(f"重复单词数量: {len(duplicates)}")
    print(f"重复行数: {total_duplicate_lines}")
    
    # 计算理论上的唯一单词数
    total_lines = 0
    with open('词库/dict', 'r', encoding='utf-8') as f:
        for line in f:
            if line.strip() and not line.strip().startswith('#'):
                total_lines += 1
    
    unique_words = total_lines - total_duplicate_lines
    print(f"dict文件总行数: {total_lines}")
    print(f"理论唯一单词数: {unique_words}")

if __name__ == "__main__":
    main()


