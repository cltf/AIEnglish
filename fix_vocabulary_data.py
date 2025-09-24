#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复词汇数据：修正词性、音标和例句
"""

import sqlite3
import re

def fix_part_of_speech(meaning):
    """根据中文意思修正词性"""
    if 'ad.' in meaning or 'adv.' in meaning:
        return 'ad.'
    elif 'prep.' in meaning:
        return 'prep.'
    elif 'conj.' in meaning:
        return 'conj.'
    elif 'pron.' in meaning:
        return 'pron.'
    elif 'num.' in meaning:
        return 'num.'
    elif 'a.' in meaning and 'n.' not in meaning:
        return 'a.'
    elif 'v.' in meaning and 'n.' not in meaning:
        return 'v.'
    elif 'n.' in meaning:
        return 'n.'
    else:
        return 'n.'  # 默认为名词

def generate_better_phonetic(word):
    """生成更准确的音标"""
    # 简单的音标生成规则
    word_lower = word.lower()
    
    # 常见音标映射
    phonetic_map = {
        'a': '/æ/',
        'e': '/e/',
        'i': '/ɪ/',
        'o': '/ɒ/',
        'u': '/ʌ/',
        'ai': '/eɪ/',
        'ay': '/eɪ/',
        'ee': '/iː/',
        'oo': '/uː/',
        'ou': '/aʊ/',
        'ow': '/aʊ/',
        'ch': '/tʃ/',
        'sh': '/ʃ/',
        'th': '/θ/',
        'ph': '/f/',
        'ck': '/k/',
        'ng': '/ŋ/'
    }
    
    # 生成音标
    phonetic = f"/{word_lower}/"
    
    # 应用一些基本规则
    if word_lower.endswith('ing'):
        phonetic = phonetic.replace('/ing/', '/ɪŋ/')
    elif word_lower.endswith('ed'):
        phonetic = phonetic.replace('/ed/', '/d/')
    elif word_lower.endswith('er'):
        phonetic = phonetic.replace('/er/', '/ə/')
    elif word_lower.endswith('ly'):
        phonetic = phonetic.replace('/ly/', '/li/')
    
    return phonetic

def generate_better_example(word, part_of_speech, meaning):
    """生成更有意义的例句"""
    word_lower = word.lower()
    
    # 根据词性生成例句
    if part_of_speech == 'v.':
        examples = [
            f"I {word_lower} every day.",
            f"She {word_lower}s well.",
            f"We {word_lower} together.",
            f"Can you {word_lower}?",
            f"They {word_lower} at school."
        ]
    elif part_of_speech == 'n.':
        examples = [
            f"This is a {word_lower}.",
            f"The {word_lower} is nice.",
            f"I have a {word_lower}.",
            f"Look at the {word_lower}.",
            f"This {word_lower} is good."
        ]
    elif part_of_speech == 'a.':
        examples = [
            f"It's very {word_lower}.",
            f"This is {word_lower}.",
            f"She looks {word_lower}.",
            f"The {word_lower} book.",
            f"I feel {word_lower}."
        ]
    elif part_of_speech == 'ad.':
        examples = [
            f"I {word_lower} like it.",
            f"She is {word_lower} good.",
            f"This is {word_lower} nice.",
            f"I {word_lower} go there.",
            f"It's {word_lower} beautiful."
        ]
    elif part_of_speech == 'prep.':
        examples = [
            f"Come {word_lower} me.",
            f"Go {word_lower} the door.",
            f"Stand {word_lower} the table.",
            f"Walk {word_lower} the park.",
            f"Sit {word_lower} the chair."
        ]
    elif part_of_speech == 'pron.':
        examples = [
            f"{word.capitalize()} are friends.",
            f"I like {word_lower}.",
            f"Give it to {word_lower}.",
            f"{word.capitalize()} is here.",
            f"I see {word_lower}."
        ]
    elif part_of_speech == 'num.':
        examples = [
            f"I have {word_lower} books.",
            f"There are {word_lower} people.",
            f"Count to {word_lower}.",
            f"I need {word_lower} apples.",
            f"Show me {word_lower}."
        ]
    else:
        examples = [
            f"This is {word_lower}.",
            f"I like {word_lower}.",
            f"The {word_lower} is good.",
            f"Look at {word_lower}.",
            f"I have {word_lower}."
        ]
    
    # 选择第一个例句
    return examples[0]

def main():
    print("=== 开始修复词汇数据 ===")
    
    try:
        print("1. 正在连接数据库...")
        conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
        cursor = conn.cursor()
        print("   ✅ 数据库连接成功")
        
        print("2. 正在查询所有词汇...")
        cursor.execute("SELECT id, word, phonetic, part_of_speech, meaning, example FROM vocabulary ORDER BY id")
        words = cursor.fetchall()
        print(f"   ✅ 查询完成，找到 {len(words)} 个词汇")
        
        print("3. 开始修复数据...")
        fixed_count = 0
        
        for i, (word_id, word, current_phonetic, current_part_of_speech, meaning, current_example) in enumerate(words, 1):
            print(f"\n--- 修复第 {i}/{len(words)} 个词汇 ---")
            print(f"单词: {word}")
            print(f"当前词性: {current_part_of_speech}")
            print(f"当前音标: {current_phonetic}")
            print(f"当前例句: {current_example}")
            
            # 修正词性
            new_part_of_speech = fix_part_of_speech(meaning)
            print(f"修正词性: {new_part_of_speech}")
            
            # 生成更好的音标
            if current_phonetic == f"/{word.lower()}/" or current_phonetic == f"/{word}/":
                new_phonetic = generate_better_phonetic(word)
                print(f"生成音标: {new_phonetic}")
            else:
                new_phonetic = current_phonetic
                print(f"保持音标: {new_phonetic}")
            
            # 生成更好的例句
            if current_example == f"I like the {word.lower()}." or current_example == f"I like the {word}.":
                new_example = generate_better_example(word, new_part_of_speech, meaning)
                print(f"生成例句: {new_example}")
            else:
                new_example = current_example
                print(f"保持例句: {new_example}")
            
            # 更新数据库
            cursor.execute("""
                UPDATE vocabulary 
                SET part_of_speech = ?, phonetic = ?, example = ?
                WHERE id = ?
            """, (new_part_of_speech, new_phonetic, new_example, word_id))
            
            print("   ✅ 数据库更新成功")
            fixed_count += 1
            
            # 每100个词汇提交一次
            if i % 100 == 0:
                print(f"\n🔄 正在提交前 {i} 个词汇的更新...")
                conn.commit()
                print(f"   ✅ 已提交 {i} 个词汇的更新")
        
        print(f"\n4. 正在提交最终更新...")
        conn.commit()
        print("   ✅ 最终更新已提交")
        
        print(f"\n5. 正在关闭数据库连接...")
        conn.close()
        print("   ✅ 数据库连接已关闭")
        
        print(f"\n=== 修复完成 ===")
        print(f"成功修复: {fixed_count} 个词汇")
        
    except Exception as e:
        print(f"❌ 发生错误: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()

