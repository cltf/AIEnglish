package com.vocabulary.scanner.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * SQLite词汇数据库助手
 * 从assets目录加载预制的1689个中考英语核心词汇数据库
 */
class VocabularyDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    
    private val context = context
    
    companion object {
        private const val DATABASE_NAME = "vocabulary.db"
        private const val DATABASE_VERSION = 4
        
        // 表名
        const val TABLE_VOCABULARY = "vocabulary"
        
        // 列名
        const val COLUMN_ID = "id"
        const val COLUMN_WORD = "word"
        const val COLUMN_MEANING = "meaning"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DIFFICULTY = "difficulty"
        const val COLUMN_TYPE = "type"
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        // 只创建表结构，不复制数据
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
        
        // 延迟复制数据，避免在onCreate中进行文件操作
        // copyDatabaseFromAssets()
    }
    
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
    
    /**
     * 从assets目录复制预制的数据库文件
     */
    private fun copyDatabaseFromAssets() {
        try {
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            
            // 如果数据库文件已存在，先删除
            if (dbFile.exists()) {
                dbFile.delete()
            }
            
            // 确保父目录存在
            dbFile.parentFile?.mkdirs()
            
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
    
    /**
     * 创建空表作为备用方案
     */
    private fun createEmptyTable() {
        val db = writableDatabase
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
        db.close()
    }
}

/**
 * 词汇数据项
 */
data class VocabularyItem(
    val word: String,
    val meaning: String,
    val category: String,
    val difficulty: String
)