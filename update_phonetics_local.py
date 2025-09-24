#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
使用本地规则生成音标和例句，更新SQLite数据库
"""

import sqlite3
import re

def generate_phonetic(word):
    """
    根据单词生成简单的音标
    """
    # 清理单词
    clean_word = word.lower().split('(')[0].strip()
    
    # 简单的音标生成规则
    phonetic_rules = {
        # 常见字母组合
        'th': 'θ',
        'ch': 'tʃ',
        'sh': 'ʃ',
        'ph': 'f',
        'gh': 'f',
        'ck': 'k',
        'qu': 'kw',
        'ng': 'ŋ',
        'tion': 'ʃən',
        'sion': 'ʒən',
        'ture': 'tʃər',
        'sure': 'ʒər',
        
        # 元音规则
        'a_e': 'eɪ',  # cake, make
        'i_e': 'aɪ',  # bike, like
        'o_e': 'oʊ',  # home, note
        'u_e': 'juː', # cute, use
        'ee': 'iː',   # see, tree
        'ea': 'iː',   # sea, tea
        'oo': 'uː',   # moon, food
        'ou': 'aʊ',   # house, mouse
        'ow': 'aʊ',   # cow, now
        'oy': 'ɔɪ',   # boy, toy
        'ay': 'eɪ',   # day, say
        'ai': 'eɪ',   # rain, train
        'oa': 'oʊ',   # boat, coat
        'ie': 'iː',   # pie, tie
        'ue': 'uː',   # blue, true
        'ar': 'ɑːr',  # car, far
        'er': 'ər',   # her, term
        'ir': 'ɜːr',  # bird, girl
        'or': 'ɔːr',  # for, more
        'ur': 'ɜːr',  # turn, burn
    }
    
    # 应用规则
    phonetic = clean_word
    for pattern, replacement in phonetic_rules.items():
        phonetic = phonetic.replace(pattern, replacement)
    
    # 添加音标符号
    phonetic = f"/{phonetic}/"
    
    return phonetic

def generate_example(word):
    """
    根据单词生成简单的例句
    """
    # 清理单词
    clean_word = word.lower().split('(')[0].strip()
    
    # 简单的例句模板
    examples = [
        f"I like the {clean_word}.",
        f"This is a {clean_word}.",
        f"The {clean_word} is good.",
        f"I have a {clean_word}.",
        f"This {clean_word} is nice.",
        f"I see the {clean_word}.",
        f"The {clean_word} is here.",
        f"I want the {clean_word}.",
        f"This {clean_word} is big.",
        f"I found the {clean_word}."
    ]
    
    # 根据单词长度选择例句
    word_len = len(clean_word)
    example_index = word_len % len(examples)
    
    return examples[example_index]

def update_database():
    """更新数据库中的音标和例句"""
    print("正在连接数据库...")
    conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
    cursor = conn.cursor()
    print("数据库连接成功!")
    
    print("正在查询所有单词...")
    # 获取所有单词
    cursor.execute("SELECT id, word, phonetic, example FROM vocabulary ORDER BY word")
    words = cursor.fetchall()
    print(f"查询完成，共找到 {len(words)} 个单词")
    
    print(f"开始更新 {len(words)} 个单词的音标和例句...")
    
    updated_count = 0
    skipped_count = 0
    
    for i, (word_id, word, current_phonetic, current_example) in enumerate(words, 1):
        print(f"处理 {i}/{len(words)}: {word}")
        
        # 如果已经有音标和例句，跳过
        if current_phonetic and current_phonetic != f"/{word}/" and current_example and current_example != f"Example with {word}.":
            print(f"  ⏭️ 跳过 (已有数据)")
            skipped_count += 1
            continue
        
        print(f"  正在生成音标和例句...")
        # 生成音标和例句
        phonetic = generate_phonetic(word)
        example = generate_example(word)
        
        print(f"  正在更新数据库...")
        # 更新数据库
        cursor.execute("""
            UPDATE vocabulary 
            SET phonetic = ?, example = ?
            WHERE id = ?
        """, (phonetic, example, word_id))
        
        print(f"  ✅ 更新成功: {phonetic} | {example}")
        updated_count += 1
        
        # 每100个单词提交一次
        if i % 100 == 0:
            print(f"正在提交 {i} 个单词的更新...")
            conn.commit()
            print(f"已提交 {i} 个单词的更新")
    
    print("正在提交最终更新...")
    # 最终提交
    conn.commit()
    conn.close()
    print("数据库连接已关闭")
    
    print(f"\n更新完成!")
    print(f"成功更新: {updated_count} 个单词")
    print(f"跳过 (已有数据): {skipped_count} 个单词")

def main():
    """主函数"""
    print("开始使用本地规则生成单词的音标和例句...")
    
    try:
        update_database()
    except Exception as e:
        print(f"更新过程中出现错误: {e}")

if __name__ == "__main__":
    main()
