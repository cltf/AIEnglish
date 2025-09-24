#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
处理词库dict文件，将词汇数据存储到SQLite数据库
修复了连字符单词的解析问题
"""

import sqlite3
import re
import os
import sys

def create_database():
    """创建SQLite数据库和表"""
    db_path = "app/src/main/assets/vocabulary.db"
    
    # 确保目录存在
    os.makedirs(os.path.dirname(db_path), exist_ok=True)
    
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # 删除现有表（如果存在）
    cursor.execute("DROP TABLE IF EXISTS vocabulary")
    
    # 创建表
    cursor.execute('''
        CREATE TABLE vocabulary (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            word TEXT UNIQUE NOT NULL,
            phonetic TEXT,
            part_of_speech TEXT,
            meaning TEXT NOT NULL,
            example TEXT,
            category TEXT,
            difficulty TEXT,
            type TEXT DEFAULT 'MIDDLE_SCHOOL'
        )
    ''')
    
    return conn, cursor

def parse_line_format3(line):
    """
    解析格式3: 单词 - 中文意思
    例如: abandon - 抛弃，放弃
    修复了连字符单词的解析问题
    """
    pattern = r'^([a-zA-Z\s\(\)\-]+)\s*-\s*(.*)'
    match = re.match(pattern, line.strip())
    
    if match:
        word, meaning = match.groups()
        # 清理单词，但保留连字符
        word = word.strip()
        
        # 基本音标（简单处理）
        phonetic = f"/{word}/"
        
        # 示例句子
        example = f"Example with {word}."
        
        # 基础分类
        category = "基础"
        difficulty = "基础"
        
        # 默认词性
        part_of_speech = "n."
        
        return (word, phonetic, part_of_speech, meaning, example, category, difficulty)
    
    return None

def process_dict_file(file_path, conn, cursor):
    """处理词库文件"""
    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        return 0
    
    inserted_count = 0
    line_count = 0
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            for line_num, line in enumerate(f, 1):
                line = line.strip()
                if not line or line.startswith('#'):
                    continue
                
                line_count += 1
                
                # 解析词汇行
                vocab_data = parse_line_format3(line)
                if vocab_data:
                    try:
                        cursor.execute('''
                            INSERT OR IGNORE INTO vocabulary 
                            (word, phonetic, part_of_speech, meaning, example, category, difficulty, type)
                            VALUES (?, ?, ?, ?, ?, ?, ?, 'MIDDLE_SCHOOL')
                        ''', vocab_data)
                        inserted_count += 1
                        
                        if inserted_count % 100 == 0:
                            print(f"已处理 {inserted_count} 个词汇...")
                            
                    except Exception as e:
                        print(f"插入词汇失败 (行 {line_num}): {line} - {e}")
                else:
                    print(f"无法解析行 {line_num}: {line}")
        
        conn.commit()
        print(f"文件处理完成: {line_count} 行，成功插入 {inserted_count} 个词汇")
        
    except Exception as e:
        print(f"处理文件时出错: {e}")
        return 0
    
    return inserted_count

def main():
    """主函数"""
    if len(sys.argv) != 2:
        print("用法: python3 process_dict_file_fixed.py <词库文件路径>")
        print("示例: python3 process_dict_file_fixed.py 词库/dict")
        return
    
    dict_file = sys.argv[1]
    
    print(f"开始处理词库文件: {dict_file}")
    
    # 创建数据库
    conn, cursor = create_database()
    
    try:
        # 处理词库文件
        count = process_dict_file(dict_file, conn, cursor)
        
        # 验证数据
        cursor.execute("SELECT COUNT(*) FROM vocabulary")
        total_count = cursor.fetchone()[0]
        print(f"数据库中总共有 {total_count} 个词汇")
        
        # 检查连字符单词
        cursor.execute("SELECT word FROM vocabulary WHERE word LIKE '%-%' ORDER BY word")
        hyphen_words = cursor.fetchall()
        print(f"包含连字符的单词数量: {len(hyphen_words)}")
        if hyphen_words:
            print("连字符单词示例:")
            for word, in hyphen_words[:10]:
                print(f"  {word}")
        
        # 显示一些示例
        cursor.execute("SELECT word, meaning FROM vocabulary ORDER BY word LIMIT 5")
        results = cursor.fetchall()
        print("示例词汇:")
        for word, meaning in results:
            print(f"  {word} - {meaning}")
            
    finally:
        conn.close()
    
    print("处理完成！")

if __name__ == "__main__":
    main()


