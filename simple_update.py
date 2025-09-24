#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简单的数据库更新脚本，每个单词都打印日志
"""

import sqlite3

def main():
    print("=== 开始更新数据库 ===")
    
    try:
        print("1. 正在连接数据库...")
        conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
        cursor = conn.cursor()
        print("   ✅ 数据库连接成功")
        
        print("2. 正在查询所有单词...")
        cursor.execute("SELECT id, word, phonetic, example FROM vocabulary ORDER BY word LIMIT 10")
        words = cursor.fetchall()
        print(f"   ✅ 查询完成，找到 {len(words)} 个单词")
        
        print("3. 开始处理单词...")
        updated_count = 0
        
        for i, (word_id, word, current_phonetic, current_example) in enumerate(words, 1):
            print(f"\n--- 处理第 {i} 个单词 ---")
            print(f"单词ID: {word_id}")
            print(f"单词: {word}")
            print(f"当前音标: {current_phonetic}")
            print(f"当前例句: {current_example}")
            
            # 生成新的音标和例句
            new_phonetic = f"/{word.lower()}/"
            new_example = f"I like the {word.lower()}."
            
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
        
        print(f"\n4. 正在提交更新...")
        conn.commit()
        print("   ✅ 更新已提交")
        
        print(f"\n5. 正在关闭数据库连接...")
        conn.close()
        print("   ✅ 数据库连接已关闭")
        
        print(f"\n=== 更新完成 ===")
        print(f"成功更新了 {updated_count} 个单词")
        
    except Exception as e:
        print(f"❌ 发生错误: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()


