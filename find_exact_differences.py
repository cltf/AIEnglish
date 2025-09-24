#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
精确找出dict文件和SQLite数据库的5个差异单词
"""

import sqlite3
import re

def get_all_dict_words():
    """获取dict文件中的所有单词（包括特殊格式的）"""
    all_words = set()
    
    with open('词库/dict', 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            
            # 尝试解析单词
            pattern = r'^([a-zA-Z\s\(\)\-…/]+)\s*-\s*(.*)'
            match = re.match(pattern, line)
            if match:
                word_part = match.groups()[0].strip()
                
                # 提取基础单词（去除括号内容）
                if '(' in word_part:
                    # 对于包含括号的，提取括号前的部分
                    base_word = word_part.split('(')[0].strip()
                    all_words.add(base_word)
                elif '=' in word_part:
                    # 对于包含等号的，提取等号前的部分
                    base_word = word_part.split('=')[0].strip()
                    all_words.add(base_word)
                else:
                    # 普通单词
                    all_words.add(word_part)
    
    return all_words

def get_sqlite_words():
    """获取SQLite中的单词"""
    conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
    cursor = conn.cursor()
    cursor.execute("SELECT word FROM vocabulary")
    sqlite_words = set(row[0] for row in cursor.fetchall())
    conn.close()
    return sqlite_words

def get_dict_word_details():
    """获取dict文件中每个单词的详细信息"""
    word_details = {}
    
    with open('词库/dict', 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            
            # 尝试解析单词
            pattern = r'^([a-zA-Z\s\(\)\-…/]+)\s*-\s*(.*)'
            match = re.match(pattern, line)
            if match:
                word_part = match.groups()[0].strip()
                meaning = match.groups()[1].strip()
                
                # 提取基础单词
                if '(' in word_part:
                    base_word = word_part.split('(')[0].strip()
                elif '=' in word_part:
                    base_word = word_part.split('=')[0].strip()
                else:
                    base_word = word_part
                
                word_details[base_word] = (line_num, line, meaning)
    
    return word_details

def main():
    """主函数"""
    print("精确找出dict文件和SQLite数据库的差异单词...")
    
    # 获取dict文件中的所有单词
    dict_words = get_all_dict_words()
    print(f"dict文件中的单词数量: {len(dict_words)}")
    
    # 获取SQLite中的单词
    sqlite_words = get_sqlite_words()
    print(f"SQLite中的单词数量: {len(sqlite_words)}")
    
    # 找出差异
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
    
    # 获取dict文件的详细信息
    word_details = get_dict_word_details()
    
    print(f"\n=== 详细分析缺失的单词 ===")
    if missing_in_sqlite:
        for word in sorted(missing_in_sqlite):
            if word in word_details:
                line_num, line, meaning = word_details[word]
                print(f"行 {line_num:4d}: {line}")
    
    print(f"\n=== 总结 ===")
    print(f"dict文件单词数: {len(dict_words)}")
    print(f"SQLite单词数: {len(sqlite_words)}")
    print(f"实际差异: {len(dict_words) - len(sqlite_words)}")

if __name__ == "__main__":
    main()


