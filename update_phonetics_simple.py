#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
使用免费API更新SQLite数据库中的音标和例句
"""

import sqlite3
import requests
import time
import json

def get_phonetic_and_example(word):
    """
    使用Free Dictionary API获取音标和例句
    """
    try:
        # 清理单词，去除括号内容
        clean_word = word.split('(')[0].strip()
        
        url = f"https://api.dictionaryapi.dev/api/v2/entries/en/{clean_word}"
        response = requests.get(url, timeout=10)
        
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
                
                return phonetic, example
                
    except Exception as e:
        print(f"搜索 {word} 时出错: {e}")
    
    return None, None

def update_database():
    """更新数据库中的音标和例句"""
    conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
    cursor = conn.cursor()
    
    # 获取所有单词
    cursor.execute("SELECT id, word, phonetic, example FROM vocabulary ORDER BY word")
    words = cursor.fetchall()
    
    print(f"开始更新 {len(words)} 个单词的音标和例句...")
    
    updated_count = 0
    skipped_count = 0
    failed_count = 0
    
    for i, (word_id, word, current_phonetic, current_example) in enumerate(words, 1):
        print(f"处理 {i}/{len(words)}: {word}")
        
        # 如果已经有音标和例句，跳过
        if current_phonetic and current_phonetic != f"/{word}/" and current_example and current_example != f"Example with {word}.":
            print(f"  ⏭️ 跳过 (已有数据)")
            skipped_count += 1
            continue
        
        # 搜索音标和例句
        phonetic, example = get_phonetic_and_example(word)
        
        if phonetic and example:
            # 更新数据库
            cursor.execute("""
                UPDATE vocabulary 
                SET phonetic = ?, example = ?
                WHERE id = ?
            """, (phonetic, example, word_id))
            
            print(f"  ✅ 更新成功: {phonetic} | {example[:50]}...")
            updated_count += 1
        else:
            print(f"  ❌ 搜索失败")
            failed_count += 1
        
        # 避免请求过于频繁
        time.sleep(0.5)
        
        # 每20个单词提交一次
        if i % 20 == 0:
            conn.commit()
            print(f"已提交 {i} 个单词的更新")
    
    # 最终提交
    conn.commit()
    conn.close()
    
    print(f"\n更新完成!")
    print(f"成功更新: {updated_count} 个单词")
    print(f"跳过 (已有数据): {skipped_count} 个单词")
    print(f"更新失败: {failed_count} 个单词")

def test_api():
    """测试API是否可用"""
    print("测试API连接...")
    try:
        response = requests.get("https://api.dictionaryapi.dev/api/v2/entries/en/hello", timeout=10)
        if response.status_code == 200:
            print("✅ API连接正常")
            return True
        else:
            print(f"❌ API返回状态码: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ API连接失败: {e}")
        return False

def main():
    """主函数"""
    print("开始从互联网搜索单词的音标和例句...")
    
    # 测试API
    if not test_api():
        print("API不可用，请检查网络连接")
        return
    
    print("注意: 这个过程可能需要较长时间，请耐心等待...")
    print("按 Ctrl+C 可以随时中断")
    
    try:
        update_database()
    except KeyboardInterrupt:
        print("\n用户中断了更新过程")
    except Exception as e:
        print(f"更新过程中出现错误: {e}")

if __name__ == "__main__":
    main()


