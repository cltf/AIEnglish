#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
特殊处理62个特殊格式的单词
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

def parse_special_line(line):
    """
    特殊处理各种格式的单词
    """
    line = line.strip()
    
    # 1. 包含括号和斜杠的：can (can't/cannot) - v. 能 / 不能
    if '(' in line and '/' in line and ')' in line:
        pattern = r'^([a-zA-Z\s]+)\s*\(([^)]+)\)\s*-\s*(.*)'
        match = re.match(pattern, line)
        if match:
            word, bracket_content, meaning = match.groups()
            word = word.strip()
            # 将括号内容添加到中文解释中
            full_meaning = f"{meaning} ({bracket_content})"
            return (word, f"/{word}/", "v.", full_meaning, f"Example with {word}.", "基础", "基础")
    
    # 2. 包含等号的：bike (=bicycle) - n. 自行车
    elif '=' in line:
        pattern = r'^([a-zA-Z\s]+)\s*\(=([^)]+)\)\s*-\s*(.*)'
        match = re.match(pattern, line)
        if match:
            word, equal_content, meaning = match.groups()
            word = word.strip()
            # 将等号内容添加到中文解释中
            full_meaning = f"{meaning} (={equal_content})"
            return (word, f"/{word}/", "n.", full_meaning, f"Example with {word}.", "基础", "基础")
        
        # 处理 lab = laboratory - n. 实验室 这种格式
        pattern2 = r'^([a-zA-Z\s]+)\s*=\s*([a-zA-Z\s]+)\s*-\s*(.*)'
        match2 = re.match(pattern2, line)
        if match2:
            word, equal_content, meaning = match2.groups()
            word = word.strip()
            full_meaning = f"{meaning} (={equal_content})"
            return (word, f"/{word}/", "n.", full_meaning, f"Example with {word}.", "基础", "基础")
    
    # 3. 包含复数标记的：leaf (复 leaves) - n.（树，菜）叶
    elif '(复' in line:
        pattern = r'^([a-zA-Z\s]+)\s*\(复\s*([^)]+)\)\s*-\s*(.*)'
        match = re.match(pattern, line)
        if match:
            word, plural_form, meaning = match.groups()
            word = word.strip()
            full_meaning = f"{meaning} (复数: {plural_form})"
            return (word, f"/{word}/", "n.", full_meaning, f"Example with {word}.", "基础", "基础")
    
    # 4. 包含比较级的：least (little 的最高级) - a.&ad. little 的最高级
    elif '比较级' in line or '最高级' in line:
        pattern = r'^([a-zA-Z\s]+)\s*\(([^)]+)\)\s*-\s*(.*)'
        match = re.match(pattern, line)
        if match:
            word, bracket_content, meaning = match.groups()
            word = word.strip()
            full_meaning = f"{meaning} ({bracket_content})"
            return (word, f"/{word}/", "a.", full_meaning, f"Example with {word}.", "基础", "基础")
    
    # 5. 包含省略号的：either…or - conj. 两者之一；要么
    elif '…' in line:
        pattern = r'^([a-zA-Z…]+)\s*-\s*(.*)'
        match = re.match(pattern, line)
        if match:
            word, meaning = match.groups()
            word = word.strip()
            return (word, f"/{word}/", "conj.", meaning, f"Example with {word}.", "基础", "基础")
    
    # 6. 包含斜杠的：electric/electronic - a. 电的；电流的
    elif '/' in line and '=' not in line:
        pattern = r'^([a-zA-Z/]+)\s*-\s*(.*)'
        match = re.match(pattern, line)
        if match:
            word, meaning = match.groups()
            word = word.strip()
            return (word, f"/{word}/", "a.", meaning, f"Example with {word}.", "基础", "基础")
    
    # 7. 包含破折号的动词变位：lay –laid- laid - v. 躺下；产卵；搁放
    elif '–' in line or '—' in line:
        pattern = r'^([a-zA-Z\s]+)\s*[–—]\s*([a-zA-Z\s\-]+)\s*-\s*(.*)'
        match = re.match(pattern, line)
        if match:
            word, verb_forms, meaning = match.groups()
            word = word.strip()
            full_meaning = f"{meaning} (变位: {verb_forms})"
            return (word, f"/{word}/", "v.", full_meaning, f"Example with {word}.", "基础", "基础")
    
    # 8. 其他特殊字符：be (am,is,are) - v. 是
    else:
        pattern = r'^([a-zA-Z\s]+)\s*\(([^)]+)\)\s*-\s*(.*)'
        match = re.match(pattern, line)
        if match:
            word, bracket_content, meaning = match.groups()
            word = word.strip()
            full_meaning = f"{meaning} ({bracket_content})"
            return (word, f"/{word}/", "v.", full_meaning, f"Example with {word}.", "基础", "基础")
    
    return None

def process_all_words():
    """处理所有单词，包括特殊格式的"""
    conn, cursor = create_database()
    
    # 特殊处理的62个单词
    special_words = [
        "can (can't/cannot) - v. 能 / 不能",
        "bike (=bicycle) - n. 自行车",
        "exam (=examination) - n. 考试，测试",
        "kilo (=kilogram) - n. 千克",
        "kilometre (=kilometer) - n. 公里；千米",
        "lab = laboratory - n. 实验室",
        "maths= mathematics - n. 数学",
        "Mom =Mum - n. 妈妈",
        "PE (=physical education) - n. 体育",
        "photo (=photograph) - n. 照片",
        "television (=TV) - n. 电视",
        "UK (= United Kingdom) - abbr. 英国",
        "US (=United States) - abbr. 美国",
        "either…or - conj. 两者之一；要么",
        "leave –left- left - v. 离开；把…… 留下",
        "electric/electronic - a. 电的；电流的",
        "policeman/policewoman - n. 警察 / 女警察",
        "lay –laid- laid - v. 躺下；产卵；搁放",
        "lend –lent- lent - vt. 借（出）",
        "read –read-read - v. 读；朗读",
        "ride –rode-ridden - v. 骑；乘车；n. 乘车旅行",
        "ring –rang-rung - v.（钟、铃等）响",
        "rise –rose- risen - vi. 上升；升起",
        "run –ran- run - vi. 跑，奔跑；（颜色）褪色",
        "see –saw-seen - vt. 看见，看到",
        "sell –sold-sold - v. 卖，售",
        "send –sent-sent - v. 打发，派遣；送，邮寄",
        "sing–sang--sung - v. 唱，唱歌",
        "take –took-taken - vt. 拿；拿走；买下",
        "teach –taught-taught - v. 教书，教",
        "tell –told-told - vt. 告诉；讲述",
        "leaf (复 leaves) - n.（树，菜）叶",
        "life (复 lives) - n. 人生；生命；生活",
        "man (复 men) - n. 成年男人；人类",
        "sheep (复 sheep) - n.（绵）羊",
        "shelf (复 shelves) - n. 架子；搁板；格层；礁",
        "tooth (复 teeth) - n. 牙齿",
        "woman (复 women) - n. 妇女，女人",
        "least (little 的最高级) - a.&ad. little 的最高级",
        "little (比较级 less, 最高级 least) - a. 小的，少的",
        "many (比较级 more, 最高级 most) - pron. 许多人、物；a. 许多",
        "more (much 或 many 的比较级) - a. & ad. 另外的；附加的",
        "most (much 或 many 的最高级) - a. & ad. 最多；n. 大部分，大多数",
        "well (比较级 better, 最高级 best) - ad. 好；令人满意地",
        "be (am,is,are) - v. 是",
        "learn (learnt, learnt ; ~ed,~ed) - vt. 学，学习",
        "make (made, made) - vt. 制造，做；使得",
        "meet (met, met) - vt. 遇见，会；集会",
        "metre (美 meter) - n. 米，公尺",
        "mistake (mistook, mistaken) - vt. 弄错；n. 错误",
        "Mr. (mister) - n. 先生 (用于姓名前)",
        "Mrs. (mistress) - n. 太太 (称呼已婚妇女)",
        "Ms. - n. 女士（用在婚姻状况不明的女子）",
        "neighbour (美 neighbor) - n. 邻居，邻人",
        "o'clock - n. 点钟",
        "programme (美 program) - n. 节目；项目",
        "realise (美 realize) - vt. 认识到，实现",
        "recognize (美 recognize) - vt. 认出，识别",
        "say (said, said) - vt. 说，讲",
        "sit (sat, sat) - vi. 坐",
        "spell (~ed, ~ed; spelt, spelt) - vt. 拼写",
        "theatre (美 theater) - n. 剧场，戏院"
    ]
    
    inserted_count = 0
    
    # 处理特殊单词
    print("处理特殊格式的单词...")
    for line in special_words:
        vocab_data = parse_special_line(line)
        if vocab_data:
            try:
                cursor.execute('''
                    INSERT OR IGNORE INTO vocabulary 
                    (word, phonetic, part_of_speech, meaning, example, category, difficulty, type)
                    VALUES (?, ?, ?, ?, ?, ?, ?, 'MIDDLE_SCHOOL')
                ''', vocab_data)
                inserted_count += 1
                print(f"插入: {vocab_data[0]} - {vocab_data[3]}")
            except Exception as e:
                print(f"插入失败: {line} - {e}")
    
    # 处理普通单词
    print("\n处理普通单词...")
    with open('词库/dict', 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            
            # 跳过已经特殊处理的单词
            if line in special_words:
                continue
            
            # 普通解析
            pattern = r'^([a-zA-Z\s\(\)\-]+)\s*-\s*(.*)'
            match = re.match(pattern, line)
            if match:
                word, meaning = match.groups()
                word = word.strip()
                
                vocab_data = (word, f"/{word}/", "n.", meaning, f"Example with {word}.", "基础", "基础")
                try:
                    cursor.execute('''
                        INSERT OR IGNORE INTO vocabulary 
                        (word, phonetic, part_of_speech, meaning, example, category, difficulty, type)
                        VALUES (?, ?, ?, ?, ?, ?, ?, 'MIDDLE_SCHOOL')
                    ''', vocab_data)
                    inserted_count += 1
                except Exception as e:
                    print(f"插入失败: {line} - {e}")
    
    conn.commit()
    print(f"\n总共插入 {inserted_count} 个词汇")
    
    # 验证数据
    cursor.execute("SELECT COUNT(*) FROM vocabulary")
    total_count = cursor.fetchone()[0]
    print(f"数据库中总共有 {total_count} 个词汇")
    
    conn.close()
    return total_count

def main():
    """主函数"""
    print("开始特殊处理62个特殊格式的单词...")
    
    total_count = process_all_words()
    
    print(f"\n处理完成！数据库中共有 {total_count} 个词汇")

if __name__ == "__main__":
    main()


