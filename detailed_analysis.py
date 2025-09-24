#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
详细分析dict文件和SQLite数据库的差异
"""

import sqlite3
import re
from collections import defaultdict

def parse_line_format3(line):
    """解析格式3: 单词 - 中文意思"""
    pattern = r'^([a-zA-Z\s\(\)]+)\s*-\s*(.*)'
    match = re.match(pattern, line.strip())
    
    if match:
        word, meaning = match.groups()
        word = word.strip()
        return word
    return None

def analyze_dict_file():
    """分析dict文件"""
    word_count = defaultdict(list)
    unparseable_lines = []
    
    with open('词库/dict', 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            
            word = parse_line_format3(line)
            if word:
                word_count[word].append((line_num, line))
            else:
                unparseable_lines.append((line_num, line))
    
    return word_count, unparseable_lines

def get_sqlite_words():
    """获取SQLite中的单词"""
    conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
    cursor = conn.cursor()
    cursor.execute("SELECT word FROM vocabulary")
    sqlite_words = set(row[0] for row in cursor.fetchall())
    conn.close()
    return sqlite_words

def main():
    """主函数"""
    print("详细分析dict文件和SQLite数据库的差异...")
    
    # 分析dict文件
    word_count, unparseable_lines = analyze_dict_file()
    
    # 获取SQLite中的单词
    sqlite_words = get_sqlite_words()
    
    print(f"\n=== dict文件分析 ===")
    print(f"总行数: 1689")
    print(f"无法解析的行数: {len(unparseable_lines)}")
    print(f"成功解析的单词数: {len(word_count)}")
    
    # 找出重复的单词
    duplicates = {word: lines for word, lines in word_count.items() if len(lines) > 1}
    print(f"重复的单词数: {len(duplicates)}")
    
    if duplicates:
        print("\n重复的单词:")
        for word, lines in duplicates.items():
            print(f"  {word}: {len(lines)}次")
            for line_num, line in lines:
                print(f"    行 {line_num}: {line}")
    
    # 计算理论上的唯一单词数
    total_duplicate_extra = sum(len(lines) - 1 for lines in duplicates.values())
    unique_words_in_dict = len(word_count)
    print(f"\n理论唯一单词数: {unique_words_in_dict}")
    print(f"重复导致的额外行数: {total_duplicate_extra}")
    
    # 找出在dict中但不在SQLite中的单词
    dict_words = set(word_count.keys())
    missing_in_sqlite = dict_words - sqlite_words
    extra_in_sqlite = sqlite_words - dict_words
    
    print(f"\n=== 差异分析 ===")
    print(f"在dict中但不在SQLite中: {len(missing_in_sqlite)}")
    if missing_in_sqlite:
        print("缺失的单词:")
        for word in sorted(missing_in_sqlite):
            print(f"  {word}")
    
    print(f"在SQLite中但不在dict中: {len(extra_in_sqlite)}")
    if extra_in_sqlite:
        print("多余的单词:")
        for word in sorted(extra_in_sqlite):
            print(f"  {word}")
    
    print(f"\n=== 总结 ===")
    print(f"dict文件总行数: 1689")
    print(f"无法解析: {len(unparseable_lines)}")
    print(f"重复单词: {len(duplicates)}")
    print(f"理论唯一单词: {unique_words_in_dict}")
    print(f"SQLite存储: {len(sqlite_words)}")
    print(f"差异: {unique_words_in_dict - len(sqlite_words)}")

if __name__ == "__main__":
    main()


