#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
处理完整的1689个中考词汇数据并存储到SQLite数据库
"""

import sqlite3
import re
import os

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

def parse_vocabulary_line(line):
    """解析单个词汇行"""
    # 匹配格式：数字.	单词 - 词性. 中文意思
    pattern = r'(\d+)\.\s*([a-zA-Z\s\(\)]+)\s*-\s*([a-z\.]+)\s*(.*)'
    match = re.match(pattern, line.strip())
    
    if match:
        number, word, part_of_speech, meaning = match.groups()
        
        # 清理单词（去除括号等）
        word = re.sub(r'\s*\([^)]*\)', '', word).strip()
        
        # 基本音标（简单处理）
        phonetic = f"/{word}/"
        
        # 示例句子
        example = f"Example with {word}."
        
        # 基础分类
        category = "基础"
        difficulty = "基础"
        
        return (word, phonetic, part_of_speech, meaning, example, category, difficulty)
    
    return None

def process_and_insert_data(conn, cursor):
    """处理词汇数据并插入数据库"""
    
    # 读取原始文件中的词汇数据
    with open('process_vocabulary.py', 'r', encoding='utf-8') as f:
        content = f.read()
    
    inserted_count = 0
    
    # 1. 处理数字格式的词汇数据（如：1. a (an) - det. 一个）
    lines = content.split('\n')
    for line in lines:
        line = line.strip()
        if not line:
            continue
            
        # 解析词汇行
        vocab_data = parse_vocabulary_line(line)
        if vocab_data:
            try:
                cursor.execute('''
                    INSERT OR IGNORE INTO vocabulary 
                    (word, phonetic, part_of_speech, meaning, example, category, difficulty, type)
                    VALUES (?, ?, ?, ?, ?, ?, ?, 'MIDDLE_SCHOOL')
                ''', vocab_data)
                inserted_count += 1
            except Exception as e:
                print(f"插入词汇失败: {line} - {e}")
    
    # 2. 处理Python元组格式的词汇数据（如：("word", "/phonetic/", "pos", "meaning", "example", "category", "difficulty")）
    import re
    tuple_pattern = r'\("([^"]+)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)"\)'
    matches = re.findall(tuple_pattern, content)
    
    for match in matches:
        word, phonetic, part_of_speech, meaning, example, category, difficulty = match
        try:
            cursor.execute('''
                INSERT OR IGNORE INTO vocabulary 
                (word, phonetic, part_of_speech, meaning, example, category, difficulty, type)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'MIDDLE_SCHOOL')
            ''', (word, phonetic, part_of_speech, meaning, example, category, difficulty))
            inserted_count += 1
        except Exception as e:
            print(f"插入词汇失败: {word} - {e}")
    
    conn.commit()
    print(f"成功插入 {inserted_count} 个词汇到数据库")

def main():
    """主函数"""
    print("开始处理完整的中考词汇数据...")
    
    # 创建数据库
    conn, cursor = create_database()
    
    try:
        # 处理并插入数据
        process_and_insert_data(conn, cursor)
        
        # 验证数据
        cursor.execute("SELECT COUNT(*) FROM vocabulary")
        count = cursor.fetchone()[0]
        print(f"数据库中总共有 {count} 个词汇")
        
        # 检查特定单词
        cursor.execute("SELECT * FROM vocabulary WHERE word IN ('zoo', 'set', 'a', 'ability')")
        results = cursor.fetchall()
        print("找到的单词:")
        for row in results:
            print(f"  {row[1]} - {row[4]}")
            
    finally:
        conn.close()
    
    print("处理完成！")

if __name__ == "__main__":
    main()