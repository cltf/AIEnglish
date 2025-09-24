package com.vocabulary.scanner.ui.import

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vocabulary.scanner.databinding.ActivityImportVocabularyBinding
import com.vocabulary.scanner.data.UnifiedVocabularyDatabase
import com.vocabulary.scanner.data.VocabularyType
import com.vocabulary.scanner.data.VocabularyDatabaseManager
import com.vocabulary.scanner.data.VocabularyDatabaseHelper

class ImportVocabularyActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityImportVocabularyBinding
    private lateinit var databaseManager: VocabularyDatabaseManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportVocabularyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化数据库
        UnifiedVocabularyDatabase.initialize(this)
        databaseManager = VocabularyDatabaseManager(this)
        databaseManager.openDatabase()
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnImport.setOnClickListener {
            importVocabulary()
        }
        
        binding.btnClear.setOnClickListener {
            clearInput()
        }
    }
    
    private fun importVocabulary() {
        val word = binding.etWord.text.toString().trim()
        val phonetic = binding.etPhonetic.text.toString().trim()
        val partOfSpeech = binding.etPartOfSpeech.text.toString().trim()
        val meaning = binding.etMeaning.text.toString().trim()
        val example = binding.etExample.text.toString().trim()
        
        if (word.isEmpty() || meaning.isEmpty()) {
            Toast.makeText(this, "单词和释义不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            // 检查单词是否已存在
            if (databaseManager.isWordExists(word)) {
                Toast.makeText(this, "单词已存在", Toast.LENGTH_SHORT).show()
                return
            }
            
            // 插入新词汇
            val success = insertVocabulary(word, phonetic, partOfSpeech, meaning, example)
            
            if (success) {
                Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show()
                clearInput()
            } else {
                Toast.makeText(this, "导入失败", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "导入出错: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun insertVocabulary(
        word: String,
        phonetic: String,
        partOfSpeech: String,
        meaning: String,
        example: String
    ): Boolean {
        return try {
            val db = databaseManager.getDatabase()
            val insertSQL = """
                INSERT INTO ${VocabularyDatabaseHelper.TABLE_VOCABULARY} 
                (${VocabularyDatabaseHelper.COLUMN_WORD}, 
                 ${VocabularyDatabaseHelper.COLUMN_MEANING}, 
                 ${VocabularyDatabaseHelper.COLUMN_CATEGORY}, 
                 ${VocabularyDatabaseHelper.COLUMN_DIFFICULTY}, 
                 ${VocabularyDatabaseHelper.COLUMN_TYPE})
                VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
            
            val statement = db?.compileStatement(insertSQL)
            statement?.bindString(1, word.lowercase())
            statement?.bindString(2, meaning)
            statement?.bindString(3, "超纲")
            statement?.bindString(4, "超纲")
            statement?.bindString(5, VocabularyType.ADVANCED.name)
            
            val result = statement?.executeInsert()
            statement?.close()
            
            result != null && result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun clearInput() {
        binding.etWord.text?.clear()
        binding.etPhonetic.text?.clear()
        binding.etPartOfSpeech.text?.clear()
        binding.etMeaning.text?.clear()
        binding.etExample.text?.clear()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        databaseManager.closeDatabase()
    }
}
