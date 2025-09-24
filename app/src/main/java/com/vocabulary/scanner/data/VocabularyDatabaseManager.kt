package com.vocabulary.scanner.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.vocabulary.scanner.data.VocabularyDatabaseHelper.Companion.COLUMN_WORD
import com.vocabulary.scanner.data.VocabularyDatabaseHelper.Companion.COLUMN_MEANING
import com.vocabulary.scanner.data.VocabularyDatabaseHelper.Companion.COLUMN_CATEGORY
import com.vocabulary.scanner.data.VocabularyDatabaseHelper.Companion.COLUMN_DIFFICULTY
import com.vocabulary.scanner.data.VocabularyDatabaseHelper.Companion.COLUMN_TYPE
import com.vocabulary.scanner.data.VocabularyDatabaseHelper.Companion.TABLE_VOCABULARY

/**
 * 词汇数据库管理器
 * 提供词汇的增删改查功能
 */
class VocabularyDatabaseManager(private val context: Context) {
    
    private val dbHelper = VocabularyDatabaseHelper(context)
    private var database: SQLiteDatabase? = null
    
    /**
     * 打开数据库
     */
    fun openDatabase() {
        database = dbHelper.writableDatabase
        
        // 检查数据库是否为空，如果为空则从assets复制数据
        if (getWordCount() == 0) {
            copyDataFromAssets()
        }
    }
    
    /**
     * 从assets目录复制数据
     */
    private fun copyDataFromAssets() {
        try {
            val dbFile = context.getDatabasePath("vocabulary.db")
            
            // 如果数据库文件已存在，先删除
            if (dbFile.exists()) {
                dbFile.delete()
            }
            
            // 确保父目录存在
            dbFile.parentFile?.mkdirs()
            
            val inputStream = context.assets.open("vocabulary.db")
            val outputStream = java.io.FileOutputStream(dbFile.absolutePath)
            
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            
            // 重新打开数据库
            database?.close()
            database = dbHelper.writableDatabase
            
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果复制失败，继续使用空数据库
        }
    }
    
    /**
     * 关闭数据库
     */
    fun closeDatabase() {
        database?.close()
    }
    
    /**
     * 获取数据库实例（用于直接操作）
     */
    fun getDatabase(): SQLiteDatabase? = database
    
    /**
     * 获取所有词汇
     */
    fun getAllWords(): List<WordDefinition> {
        val words = mutableListOf<WordDefinition>()
        val cursor = database?.query(
            TABLE_VOCABULARY,
            null, null, null, null, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val word = it.getString(it.getColumnIndexOrThrow(COLUMN_WORD))
                val meaning = it.getString(it.getColumnIndexOrThrow(COLUMN_MEANING))
                
                words.add(
                    WordDefinition(
                        word = word,
                        phonetic = "",
                        definitions = listOf(Definition("", meaning ?: "")),
                        example = ""
                    )
                )
            }
        }
        
        return words
    }
    
    /**
     * 根据单词查询释义
     */
    fun getDefinition(word: String): WordDefinition? {
        val cursor = database?.query(
            TABLE_VOCABULARY,
            null,
            "$COLUMN_WORD = ?",
            arrayOf(word.lowercase()),
            null, null, null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val meaning = it.getString(it.getColumnIndexOrThrow(COLUMN_MEANING))
                
                return WordDefinition(
                    word = word,
                    phonetic = "",
                    definitions = listOf(Definition("", meaning ?: "")),
                    example = ""
                )
            }
        }
        
        return null
    }
    
    /**
     * 获取词汇集合
     */
    fun getVocabularySet(): Set<String> {
        val words = mutableSetOf<String>()
        val cursor = database?.query(
            TABLE_VOCABULARY,
            arrayOf(COLUMN_WORD),
            null, null, null, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val word = it.getString(it.getColumnIndexOrThrow(COLUMN_WORD))
                words.add(word.lowercase())
            }
        }
        
        return words
    }
    
    /**
     * 按类别查询词汇
     */
    fun getWordsByCategory(category: String): List<WordDefinition> {
        val words = mutableListOf<WordDefinition>()
        val cursor = database?.query(
            TABLE_VOCABULARY,
            null,
            "$COLUMN_CATEGORY = ?",
            arrayOf(category),
            null, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val word = it.getString(it.getColumnIndexOrThrow(COLUMN_WORD))
                val meaning = it.getString(it.getColumnIndexOrThrow(COLUMN_MEANING))
                
                words.add(
                    WordDefinition(
                        word = word,
                        phonetic = "",
                        definitions = listOf(Definition("", meaning ?: "")),
                        example = ""
                    )
                )
            }
        }
        
        return words
    }
    
    /**
     * 按词汇类型查询词汇
     */
    fun getWordsByType(type: VocabularyType): List<WordDefinition> {
        val words = mutableListOf<WordDefinition>()
        val whereClause = when (type) {
            VocabularyType.ALL -> null
            else -> "$COLUMN_TYPE = ?"
        }
        val whereArgs = when (type) {
            VocabularyType.ALL -> null
            else -> arrayOf(type.name)
        }
        
        val cursor = database?.query(
            TABLE_VOCABULARY,
            null,
            whereClause,
            whereArgs,
            null, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val word = it.getString(it.getColumnIndexOrThrow(COLUMN_WORD))
                val meaning = it.getString(it.getColumnIndexOrThrow(COLUMN_MEANING))
                
                words.add(
                    WordDefinition(
                        word = word,
                        phonetic = "",
                        definitions = listOf(Definition("", meaning ?: "")),
                        example = ""
                    )
                )
            }
        }
        
        return words
    }
    
    /**
     * 按难度查询词汇
     */
    fun getWordsByDifficulty(difficulty: String): List<WordDefinition> {
        val words = mutableListOf<WordDefinition>()
        val cursor = database?.query(
            TABLE_VOCABULARY,
            null,
            "$COLUMN_DIFFICULTY = ?",
            arrayOf(difficulty),
            null, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val word = it.getString(it.getColumnIndexOrThrow(COLUMN_WORD))
                val meaning = it.getString(it.getColumnIndexOrThrow(COLUMN_MEANING))
                
                words.add(
                    WordDefinition(
                        word = word,
                        phonetic = "",
                        definitions = listOf(Definition("", meaning ?: "")),
                        example = ""
                    )
                )
            }
        }
        
        return words
    }
    
    /**
     * 获取首页词汇（中考词汇和超纲词汇）
     */
    fun getHomePageWords(): List<WordDefinition> {
        val words = mutableListOf<WordDefinition>()
        val cursor = database?.query(
            TABLE_VOCABULARY,
            null,
            "$COLUMN_TYPE IN (?, ?)",
            arrayOf(VocabularyType.MIDDLE_SCHOOL.name, VocabularyType.ADVANCED.name),
            null, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val word = it.getString(it.getColumnIndexOrThrow(COLUMN_WORD))
                val meaning = it.getString(it.getColumnIndexOrThrow(COLUMN_MEANING))
                
                words.add(
                    WordDefinition(
                        word = word,
                        phonetic = "",
                        definitions = listOf(Definition("", meaning ?: "")),
                        example = ""
                    )
                )
            }
        }
        
        return words
    }
    
    /**
     * 搜索词汇
     */
    fun searchWords(keyword: String): List<WordDefinition> {
        val words = mutableListOf<WordDefinition>()
        val cursor = database?.query(
            TABLE_VOCABULARY,
            null,
            "$COLUMN_WORD LIKE ? OR $COLUMN_MEANING LIKE ?",
            arrayOf("%$keyword%", "%$keyword%"),
            null, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val word = it.getString(it.getColumnIndexOrThrow(COLUMN_WORD))
                val meaning = it.getString(it.getColumnIndexOrThrow(COLUMN_MEANING))
                
                words.add(
                    WordDefinition(
                        word = word,
                        phonetic = "",
                        definitions = listOf(Definition("", meaning ?: "")),
                        example = ""
                    )
                )
            }
        }
        
        return words
    }
    
    /**
     * 获取所有类别
     */
    fun getAllCategories(): List<String> {
        val categories = mutableSetOf<String>()
        val cursor = database?.query(
            TABLE_VOCABULARY,
            arrayOf(COLUMN_CATEGORY),
            null, null, COLUMN_CATEGORY, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY))
                if (!category.isNullOrEmpty()) {
                    categories.add(category)
                }
            }
        }
        
        return categories.toList()
    }
    
    /**
     * 获取所有难度级别
     */
    fun getAllDifficulties(): List<String> {
        val difficulties = mutableSetOf<String>()
        val cursor = database?.query(
            TABLE_VOCABULARY,
            arrayOf(COLUMN_DIFFICULTY),
            null, null, COLUMN_DIFFICULTY, null, null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val difficulty = it.getString(it.getColumnIndexOrThrow(COLUMN_DIFFICULTY))
                if (!difficulty.isNullOrEmpty()) {
                    difficulties.add(difficulty)
                }
            }
        }
        
        return difficulties.toList()
    }
    
    /**
     * 获取词汇总数
     */
    fun getWordCount(): Int {
        val cursor = database?.query(
            TABLE_VOCABULARY,
            arrayOf("COUNT(*) as count"),
            null, null, null, null, null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getInt(0)
            }
        }
        
        return 0
    }
    
    /**
     * 检查词汇是否存在
     */
    fun isWordExists(word: String): Boolean {
        val cursor = database?.query(
            TABLE_VOCABULARY,
            arrayOf(COLUMN_WORD),
            "$COLUMN_WORD = ?",
            arrayOf(word.lowercase()),
            null, null, null, "1"
        )
        
        cursor?.use {
            return it.count > 0
        }
        
        return false
    }
}