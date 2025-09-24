#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
处理词库dict文件，将词汇数据存储到SQLite数据库
支持多种格式的词库文件
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

def parse_line_format1(line):
    """
    解析格式1: 数字. 单词 - 词性. 中文意思
    例如: 1. a (an) - det. 一个
    """
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

def parse_line_format2(line):
    """
    解析格式2: 单词 音标 词性 中文意思
    例如: abandon /əˈbændən/ vt. 抛弃，放弃
    """
    pattern = r'^([a-zA-Z\s\(\)]+)\s+([\/\[].*?[\/\]])\s+([a-z\.]+)\s+(.*)'
    match = re.match(pattern, line.strip())
    
    if match:
        word, phonetic, part_of_speech, meaning = match.groups()
        
        # 清理单词
        word = word.strip()
        
        # 示例句子
        example = f"Example with {word}."
        
        # 基础分类
        category = "基础"
        difficulty = "基础"
        
        return (word, phonetic, part_of_speech, meaning, example, category, difficulty)
    
    return None

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
        
        # 基本音标
        phonetic = f"/{word}/"
        
        # 默认词性
        part_of_speech = "n."
        
        # 示例句子
        example = f"Example with {word}."
        
        # 基础分类
        category = "基础"
        difficulty = "基础"
        
        return (word, phonetic, part_of_speech, meaning, example, category, difficulty)
    
    return None

def parse_line_format4(line):
    """
    解析格式4: 单词\t音标\t词性\t中文意思\t例句\t分类\t难度
    例如: abandon\t/əˈbændən/\tvt.\t抛弃，放弃\tHe abandoned his car.\t动作\t中级
    """
    parts = line.strip().split('\t')
    if len(parts) >= 4:
        word = parts[0].strip()
        phonetic = parts[1].strip() if len(parts) > 1 else f"/{word}/"
        part_of_speech = parts[2].strip() if len(parts) > 2 else "n."
        meaning = parts[3].strip()
        example = parts[4].strip() if len(parts) > 4 else f"Example with {word}."
        category = parts[5].strip() if len(parts) > 5 else "基础"
        difficulty = parts[6].strip() if len(parts) > 6 else "基础"
        
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
                
                # 尝试不同的解析格式
                vocab_data = None
                
                # 格式1: 数字. 单词 - 词性. 中文意思
                if re.match(r'^\d+\.\s', line):
                    vocab_data = parse_line_format1(line)
                
                # 格式2: 单词 音标 词性 中文意思
                elif re.search(r'[\/\[].*?[\/\]]', line):
                    vocab_data = parse_line_format2(line)
                
                # 格式3: 单词 - 中文意思
                elif ' - ' in line:
                    vocab_data = parse_line_format3(line)
                
                # 格式4: 制表符分隔
                elif '\t' in line:
                    vocab_data = parse_line_format4(line)
                
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
        print("用法: python3 process_dict_file.py <词库文件路径>")
        print("示例: python3 process_dict_file.py 词库/dict")
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


