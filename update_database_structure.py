#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
调整数据库结构：删除词性、音标和例句字段
"""

import sqlite3
import os

def backup_database():
    """备份原数据库"""
    print("1. 正在备份原数据库...")
    if os.path.exists('app/src/main/assets/vocabulary.db'):
        import shutil
        shutil.copy('app/src/main/assets/vocabulary.db', 'app/src/main/assets/vocabulary_backup.db')
        print("   ✅ 数据库备份完成: vocabulary_backup.db")
    else:
        print("   ❌ 原数据库文件不存在")

def update_database_structure():
    """更新数据库结构"""
    print("2. 正在更新数据库结构...")
    
    try:
        conn = sqlite3.connect('app/src/main/assets/vocabulary.db')
        cursor = conn.cursor()
        
        # 创建新表结构（不包含词性、音标、例句）
        print("   正在创建新表结构...")
        cursor.execute("""
            CREATE TABLE vocabulary_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                word TEXT UNIQUE NOT NULL,
                meaning TEXT NOT NULL,
                category TEXT,
                difficulty TEXT,
                type TEXT DEFAULT 'MIDDLE_SCHOOL'
            )
        """)
        
        # 复制数据到新表（只复制需要的字段）
        print("   正在复制数据到新表...")
        cursor.execute("""
            INSERT INTO vocabulary_new (id, word, meaning, category, difficulty, type)
            SELECT id, word, meaning, category, difficulty, type
            FROM vocabulary
        """)
        
        # 删除旧表
        print("   正在删除旧表...")
        cursor.execute("DROP TABLE vocabulary")
        
        # 重命名新表
        print("   正在重命名新表...")
        cursor.execute("ALTER TABLE vocabulary_new RENAME TO vocabulary")
        
        # 提交更改
        conn.commit()
        print("   ✅ 数据库结构更新完成")
        
        # 验证新表结构
        print("   正在验证新表结构...")
        cursor.execute("PRAGMA table_info(vocabulary)")
        columns = cursor.fetchall()
        print("   新表结构:")
        for col in columns:
            print(f"     - {col[1]} ({col[2]})")
        
        # 验证数据数量
        cursor.execute("SELECT COUNT(*) FROM vocabulary")
        count = cursor.fetchone()[0]
        print(f"   数据数量: {count} 条记录")
        
        conn.close()
        print("   ✅ 数据库连接已关闭")
        
    except Exception as e:
        print(f"   ❌ 更新失败: {e}")
        import traceback
        traceback.print_exc()

def main():
    print("=== 开始调整数据库结构 ===")
    print("将删除以下字段:")
    print("  - part_of_speech (词性)")
    print("  - phonetic (音标)")
    print("  - example (例句)")
    print("保留以下字段:")
    print("  - id (主键)")
    print("  - word (单词)")
    print("  - meaning (中文意思)")
    print("  - category (分类)")
    print("  - difficulty (难度)")
    print("  - type (类型)")
    print()
    
    # 备份数据库
    backup_database()
    
    # 更新数据库结构
    update_database_structure()
    
    print("\n=== 数据库结构调整完成 ===")
    print("原数据库已备份为: vocabulary_backup.db")
    print("新数据库结构已更新完成")

if __name__ == "__main__":
    main()

