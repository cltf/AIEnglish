#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
更新所有单词的音标和例句脚本，每个单词都打印详细日志
"""

import sqlite3

def generate_phonetic(word):
    """生成音标"""
    return f"/{word.lower()}/"

def generate_example(word):
    """生成例句"""
    return f"I like the {word.lower()}."

def main():
    print("=== 开始更新所有单词的音标和例句 ===")
    
    try:
        print("1. 正在连接数据库...")
        conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
        cursor = conn.cursor()
        print("   ✅ 数据库连接成功")
        
        print("2. 正在查询所有单词...")
        cursor.execute("SELECT id, word, phonetic, example FROM vocabulary ORDER BY word")
        words = cursor.fetchall()
        print(f"   ✅ 查询完成，找到 {len(words)} 个单词")
        
        print("3. 开始处理单词...")
        updated_count = 0
        skipped_count = 0
        
        for i, (word_id, word, current_phonetic, current_example) in enumerate(words, 1):
            print(f"\n--- 处理第 {i}/{len(words)} 个单词 ---")
            print(f"单词ID: {word_id}")
            print(f"单词: {word}")
            print(f"当前音标: {current_phonetic}")
            print(f"当前例句: {current_example}")
            
            # 检查是否已经有非占位符数据
            if (current_phonetic and current_phonetic != f"/{word}/" and 
                current_example and current_example != f"Example with {word}."):
                print("   ⏭️ 跳过 (已有非占位符数据)")
                skipped_count += 1
                continue
            
            # 生成新的音标和例句
            new_phonetic = generate_phonetic(word)
            new_example = generate_example(word)
            
            print(f"新音标: {new_phonetic}")
            print(f"新例句: {new_example}")
            
            # 更新数据库
            print("正在更新数据库...")
            cursor.execute("""
                UPDATE vocabulary 
                SET phonetic = ?, example = ?
                WHERE id = ?
            """, (new_phonetic, new_example, word_id))
            
            print("   ✅ 数据库更新成功")
            updated_count += 1
            
            # 每100个单词提交一次
            if i % 100 == 0:
                print(f"\n🔄 正在提交前 {i} 个单词的更新...")
                conn.commit()
                print(f"   ✅ 已提交 {i} 个单词的更新")
        
        print(f"\n4. 正在提交最终更新...")
        conn.commit()
        print("   ✅ 最终更新已提交")
        
        print(f"\n5. 正在关闭数据库连接...")
        conn.close()
        print("   ✅ 数据库连接已关闭")
        
        print(f"\n=== 更新完成 ===")
        print(f"总单词数: {len(words)}")
        print(f"成功更新: {updated_count} 个单词")
        print(f"跳过 (已有数据): {skipped_count} 个单词")
        
    except Exception as e:
        print(f"❌ 发生错误: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()

