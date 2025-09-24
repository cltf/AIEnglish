#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
详细对比dict文件和SQLite数据库，找出所有差异
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
            else:
                # 记录无法解析的行
                print(f"无法解析行 {line_num}: {line}")
    
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
    print("开始详细对比dict文件和SQLite数据库...")
    
    # 获取dict文件中的单词
    print("\n=== 解析dict文件 ===")
    dict_words = get_dict_words()
    print(f"dict文件中成功解析的单词数量: {len(dict_words)}")
    
    # 获取SQLite中的单词
    print("\n=== 获取SQLite数据 ===")
    sqlite_words = get_sqlite_words()
    print(f"SQLite中的单词数量: {len(sqlite_words)}")
    
    # 找出在dict中但不在SQLite中的单词
    print("\n=== 找出差异 ===")
    missing_words = []
    for line_num, word, original_line in dict_words:
        if word not in sqlite_words:
            missing_words.append((line_num, word, original_line))
    
    print(f"在dict中但不在SQLite中的单词数量: {len(missing_words)}")
    if missing_words:
        print("\n在dict中但不在SQLite中的单词:")
        print("-" * 80)
        for line_num, word, original_line in missing_words:
            print(f"行 {line_num:4d}: {original_line}")
        print("-" * 80)
    
    # 找出在SQLite中但不在dict中的单词
    dict_word_set = set(word for _, word, _ in dict_words)
    extra_words = sqlite_words - dict_word_set
    print(f"\n在SQLite中但不在dict中的单词数量: {len(extra_words)}")
    if extra_words:
        print("在SQLite中但不在dict中的单词:")
        for word in sorted(extra_words):
            print(f"  {word}")
    
    print(f"\n=== 总结 ===")
    print(f"dict文件总行数: 1689")
    print(f"dict文件成功解析: {len(dict_words)}")
    print(f"SQLite存储: {len(sqlite_words)}")
    print(f"差异: {1689 - len(sqlite_words)}")

if __name__ == "__main__":
    main()


