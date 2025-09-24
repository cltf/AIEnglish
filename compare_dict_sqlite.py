#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
对比dict文件和SQLite数据库，找出无法解析的单词
"""

import sqlite3
import re

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

def get_dict_words():
    """从dict文件中提取所有单词"""
    dict_words = []
    with open('词库/dict', 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            
            word = parse_line_format3(line)
            if word:
                dict_words.append((line_num, word, line))
    
    return dict_words

def get_sqlite_words():
    """从SQLite数据库中获取所有单词"""
    conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
    cursor = conn.cursor()
    
    cursor.execute("SELECT word FROM vocabulary")
    sqlite_words = set(row[0] for row in cursor.fetchall())
    
    conn.close()
    return sqlite_words

def main():
    """主函数"""
    print("开始对比dict文件和SQLite数据库...")
    
    # 获取dict文件中的单词
    dict_words = get_dict_words()
    print(f"dict文件中的单词数量: {len(dict_words)}")
    
    # 获取SQLite中的单词
    sqlite_words = get_sqlite_words()
    print(f"SQLite中的单词数量: {len(sqlite_words)}")
    
    # 找出在dict中但不在SQLite中的单词
    missing_words = []
    for line_num, word, original_line in dict_words:
        if word not in sqlite_words:
            missing_words.append((line_num, word, original_line))
    
    print(f"\n无法解析的单词数量: {len(missing_words)}")
    print("\n无法解析的单词列表:")
    print("-" * 80)
    
    for line_num, word, original_line in missing_words:
        print(f"行 {line_num:4d}: {original_line}")
    
    print("-" * 80)
    print(f"总计: {len(missing_words)} 个单词无法解析")

if __name__ == "__main__":
    main()


