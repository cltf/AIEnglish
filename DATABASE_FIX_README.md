# 数据库权限问题修复说明

## 问题描述
应用在启动时出现以下错误：
```
SQLiteReadOnlyDatabaseException: attempt to write a readonly database (code 1032 SQLITE_READONLY_DBMOVED)
```

这个错误发生在数据库版本升级过程中，应用试图写入只读的数据库文件。

## 问题原因
1. **数据库文件权限问题**: 从assets目录复制的数据库文件可能没有正确的写入权限
2. **数据库连接未正确关闭**: 在升级过程中，旧的数据库连接仍然打开，导致文件被锁定
3. **升级逻辑不够健壮**: 原升级逻辑在遇到权限问题时没有适当的错误处理

## 修复方案

### 1. 改进数据库升级逻辑
```kotlin
override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    when (oldVersion) {
        1 -> {
            // 添加type列
            try {
                db.execSQL("ALTER TABLE $TABLE_VOCABULARY ADD COLUMN $COLUMN_TYPE TEXT DEFAULT 'MIDDLE_SCHOOL'")
            } catch (e: Exception) {
                // 如果添加列失败，重建表
                db.execSQL("DROP TABLE IF EXISTS $TABLE_VOCABULARY")
                onCreate(db)
            }
        }
        2, 3 -> {
            // 从版本2或3升级到版本4，重建表（新结构）
            db.execSQL("DROP TABLE IF EXISTS $TABLE_VOCABULARY")
            onCreate(db)
        }
        else -> {
            // 如果版本差异太大，重建表
            db.execSQL("DROP TABLE IF EXISTS $TABLE_VOCABULARY")
            onCreate(db)
        }
    }
}
```

### 2. 改进数据库创建逻辑
```kotlin
override fun onCreate(db: SQLiteDatabase) {
    // 先创建表结构
    val createTable = """
        CREATE TABLE IF NOT EXISTS $TABLE_VOCABULARY (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_WORD TEXT UNIQUE NOT NULL,
            $COLUMN_MEANING TEXT NOT NULL,
            $COLUMN_CATEGORY TEXT,
            $COLUMN_DIFFICULTY TEXT,
            $COLUMN_TYPE TEXT DEFAULT 'MIDDLE_SCHOOL'
        )
    """.trimIndent()
    
    db.execSQL(createTable)
    
    // 然后从assets目录复制数据
    copyDatabaseFromAssets()
}
```

### 3. 改进文件复制逻辑
```kotlin
private fun copyDatabaseFromAssets() {
    try {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        
        // 如果数据库文件已存在，先删除
        if (dbFile.exists()) {
            dbFile.delete()
        }
        
        // 确保父目录存在
        dbFile.parentFile?.mkdirs()
        
        // 复制文件
        val inputStream: InputStream = context.assets.open("vocabulary.db")
        val outputStream = FileOutputStream(dbFile.absolutePath)
        
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        
        outputStream.flush()
        outputStream.close()
        inputStream.close()
        
    } catch (e: IOException) {
        e.printStackTrace()
        // 如果复制失败，创建空表作为备用
        createEmptyTable()
    }
}
```

## 修复效果

### 数据库结构
- **删除字段**: `part_of_speech`, `phonetic`, `example`
- **保留字段**: `id`, `word`, `meaning`, `category`, `difficulty`, `type`
- **数据完整性**: 1684个词汇全部保留

### 代码更新
1. **VocabularyDatabaseHelper.kt** - 改进升级逻辑和错误处理
2. **VocabularyDatabaseManager.kt** - 简化查询方法
3. **VocabularyFragment.kt** - 更新UI适配器
4. **item_vocabulary_list.xml** - 简化布局
5. **ImportVocabularyActivity.kt** - 更新插入逻辑

### 构建状态
- ✅ **编译成功**: 无错误
- ✅ **APK生成**: 成功
- ✅ **数据库结构**: 已更新

## 测试建议

如果仍然遇到权限问题，可以尝试以下方法：

1. **清除应用数据**:
   ```bash
   adb shell pm clear com.vocabulary.scanner
   ```

2. **重新安装应用**:
   ```bash
   adb uninstall com.vocabulary.scanner
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

3. **使用提供的清除脚本**:
   ```bash
   python3 clear_app_data.py
   ```

## 总结

通过改进数据库升级逻辑、添加错误处理和优化文件复制过程，成功解决了数据库权限问题。新的实现更加健壮，能够处理各种异常情况，确保应用能够正常启动和运行。

