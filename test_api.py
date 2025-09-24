#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试API连接和更新几个单词
"""

import sqlite3
import requests
import time

def test_single_word(word):
    """测试单个单词的API调用"""
    try:
        print(f"测试单词: {word}")
        url = f"https://api.dictionaryapi.dev/api/v2/entries/en/{word}"
        response = requests.get(url, timeout=5)
        
        if response.status_code == 200:
            data = response.json()
            if data and len(data) > 0:
                entry = data[0]
                
                # 获取音标
                phonetic = None
                if 'phonetic' in entry and entry['phonetic']:
                    phonetic = entry['phonetic']
                elif 'phonetics' in entry and entry['phonetics']:
                    for ph in entry['phonetics']:
                        if 'text' in ph and ph['text']:
                            phonetic = ph['text']
                            break
                
                # 获取例句
                example = None
                if 'meanings' in entry and entry['meanings']:
                    for meaning in entry['meanings']:
                        if 'definitions' in meaning and meaning['definitions']:
                            for definition in meaning['definitions']:
                                if 'example' in definition and definition['example']:
                                    example = definition['example']
                                    break
                        if example:
                            break
                
                print(f"  ✅ 成功: {phonetic} | {example}")
                return phonetic, example
            else:
                print(f"  ❌ 无数据")
                return None, None
        else:
            print(f"  ❌ 状态码: {response.status_code}")
            return None, None
            
    except Exception as e:
        print(f"  ❌ 错误: {e}")
        return None, None

def update_few_words():
    """更新几个测试单词"""
    # 测试几个单词
    test_words = ['hello', 'fear', 'zoo', 'set', 'ice']
    
    print("测试API连接...")
    for word in test_words:
        phonetic, example = test_single_word(word)
        time.sleep(1)  # 避免请求过快
    
    print("\n开始更新数据库中的几个单词...")
    
    conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
    cursor = conn.cursor()
    
    updated_count = 0
    
    for word in test_words:
        print(f"更新单词: {word}")
        
        # 搜索音标和例句
        phonetic, example = test_single_word(word)
        
        if phonetic and example:
            # 更新数据库
            cursor.execute("""
                UPDATE vocabulary 
                SET phonetic = ?, example = ?
                WHERE word = ?
            """, (phonetic, example, word))
            
            print(f"  ✅ 数据库更新成功")
            updated_count += 1
        else:
            print(f"  ❌ 数据库更新失败")
        
        time.sleep(1)
    
    conn.commit()
    conn.close()
    
    print(f"\n更新完成! 成功更新了 {updated_count} 个单词")

if __name__ == "__main__":
    update_few_words()


