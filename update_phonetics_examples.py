#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从互联网搜索单词的音标和例句，更新SQLite数据库
"""

import sqlite3
import requests
import time
import re
from bs4 import BeautifulSoup
import json

def get_phonetic_and_example(word):
    """
    从互联网搜索单词的音标和例句
    使用多个数据源来提高成功率
    """
    try:
        # 方法1: 使用Free Dictionary API
        phonetic, example = get_from_free_dictionary(word)
        if phonetic and example:
            return phonetic, example
        
        # 方法2: 使用Cambridge Dictionary
        phonetic, example = get_from_cambridge(word)
        if phonetic and example:
            return phonetic, example
        
        # 方法3: 使用Oxford Dictionary
        phonetic, example = get_from_oxford(word)
        if phonetic and example:
            return phonetic, example
        
        # 方法4: 使用Merriam-Webster
        phonetic, example = get_from_merriam_webster(word)
        if phonetic and example:
            return phonetic, example
        
        return None, None
        
    except Exception as e:
        print(f"搜索 {word} 时出错: {e}")
        return None, None

def get_from_free_dictionary(word):
    """从Free Dictionary获取音标和例句"""
    try:
        url = f"https://api.dictionaryapi.dev/api/v2/entries/en/{word}"
        response = requests.get(url, timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            if data and len(data) > 0:
                entry = data[0]
                
                # 获取音标
                phonetic = None
                if 'phonetic' in entry:
                    phonetic = entry['phonetic']
                elif 'phonetics' in entry and entry['phonetics']:
                    phonetic = entry['phonetics'][0].get('text', '')
                
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
                
                return phonetic, example
    except Exception as e:
        print(f"Free Dictionary API 错误: {e}")
    
    return None, None

def get_from_cambridge(word):
    """从Cambridge Dictionary获取音标和例句"""
    try:
        url = f"https://dictionary.cambridge.org/dictionary/english/{word}"
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        
        response = requests.get(url, headers=headers, timeout=10)
        if response.status_code == 200:
            soup = BeautifulSoup(response.content, 'html.parser')
            
            # 获取音标
            phonetic = None
            phonetic_elem = soup.find('span', class_='ipa')
            if phonetic_elem:
                phonetic = phonetic_elem.get_text().strip()
            
            # 获取例句
            example = None
            example_elem = soup.find('span', class_='eg')
            if example_elem:
                example = example_elem.get_text().strip()
            
            return phonetic, example
    except Exception as e:
        print(f"Cambridge Dictionary 错误: {e}")
    
    return None, None

def get_from_oxford(word):
    """从Oxford Dictionary获取音标和例句"""
    try:
        url = f"https://www.oxfordlearnersdictionaries.com/definition/english/{word}"
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        
        response = requests.get(url, headers=headers, timeout=10)
        if response.status_code == 200:
            soup = BeautifulSoup(response.content, 'html.parser')
            
            # 获取音标
            phonetic = None
            phonetic_elem = soup.find('span', class_='phon')
            if phonetic_elem:
                phonetic = phonetic_elem.get_text().strip()
            
            # 获取例句
            example = None
            example_elem = soup.find('span', class_='x')
            if example_elem:
                example = example_elem.get_text().strip()
            
            return phonetic, example
    except Exception as e:
        print(f"Oxford Dictionary 错误: {e}")
    
    return None, None

def get_from_merriam_webster(word):
    """从Merriam-Webster获取音标和例句"""
    try:
        url = f"https://www.merriam-webster.com/dictionary/{word}"
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        
        response = requests.get(url, headers=headers, timeout=10)
        if response.status_code == 200:
            soup = BeautifulSoup(response.content, 'html.parser')
            
            # 获取音标
            phonetic = None
            phonetic_elem = soup.find('span', class_='pr')
            if phonetic_elem:
                phonetic = phonetic_elem.get_text().strip()
            
            # 获取例句
            example = None
            example_elem = soup.find('span', class_='vi')
            if example_elem:
                example = example_elem.get_text().strip()
            
            return phonetic, example
    except Exception as e:
        print(f"Merriam-Webster 错误: {e}")
    
    return None, None

def update_database():
    """更新数据库中的音标和例句"""
    conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
    cursor = conn.cursor()
    
    # 获取所有单词
    cursor.execute("SELECT id, word FROM vocabulary ORDER BY word")
    words = cursor.fetchall()
    
    print(f"开始更新 {len(words)} 个单词的音标和例句...")
    
    updated_count = 0
    failed_count = 0
    
    for i, (word_id, word) in enumerate(words, 1):
        print(f"处理 {i}/{len(words)}: {word}")
        
        # 搜索音标和例句
        phonetic, example = get_phonetic_and_example(word)
        
        if phonetic and example:
            # 更新数据库
            cursor.execute("""
                UPDATE vocabulary 
                SET phonetic = ?, example = ?
                WHERE id = ?
            """, (phonetic, example, word_id))
            
            print(f"  ✅ 更新成功: {phonetic} | {example}")
            updated_count += 1
        else:
            print(f"  ❌ 搜索失败")
            failed_count += 1
        
        # 避免请求过于频繁
        time.sleep(1)
        
        # 每10个单词提交一次
        if i % 10 == 0:
            conn.commit()
            print(f"已提交 {i} 个单词的更新")
    
    # 最终提交
    conn.commit()
    conn.close()
    
    print(f"\n更新完成!")
    print(f"成功更新: {updated_count} 个单词")
    print(f"更新失败: {failed_count} 个单词")

def main():
    """主函数"""
    print("开始从互联网搜索单词的音标和例句...")
    print("注意: 这个过程可能需要较长时间，请耐心等待...")
    
    try:
        update_database()
    except KeyboardInterrupt:
        print("\n用户中断了更新过程")
    except Exception as e:
        print(f"更新过程中出现错误: {e}")

if __name__ == "__main__":
    main()


