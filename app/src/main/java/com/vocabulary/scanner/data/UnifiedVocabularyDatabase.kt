package com.vocabulary.scanner.data

import android.content.Context

/**
 * 统一词汇数据库
 * 整合SQLite数据库和内存数据库，提供统一的接口
 */
object UnifiedVocabularyDatabase {
    
    private var context: Context? = null
    private var databaseManager: VocabularyDatabaseManager? = null
    
    /**
     * 初始化数据库
     */
    fun initialize(appContext: Context) {
        try {
            context = appContext.applicationContext
            databaseManager = VocabularyDatabaseManager(context!!)
            databaseManager?.openDatabase()
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果初始化失败，设置为null，后续操作会返回空结果
            databaseManager = null
        }
    }
    
    /**
     * 确保数据库已初始化
     */
    private fun ensureInitialized(context: Context) {
        if (databaseManager == null) {
            initialize(context)
        }
    }
    
    /**
     * 获取词汇集合
     */
    fun getVocabularySet(): Set<String> {
        return databaseManager?.getVocabularySet() ?: emptySet()
    }
    
    /**
     * 根据单词获取释义
     */
    fun getDefinition(word: String): WordDefinition? {
        return databaseManager?.getDefinition(word)
    }
    
    /**
     * 获取所有词汇数据
     */
    fun getAllWords(): List<WordDefinition> {
        return databaseManager?.getAllWords() ?: emptyList()
    }
    
    /**
     * 按类别获取词汇
     */
    fun getWordsByCategory(category: String): List<WordDefinition> {
        return databaseManager?.getWordsByCategory(category) ?: emptyList()
    }
    
    /**
     * 按词汇类型获取词汇
     */
    fun getWordsByType(type: VocabularyType): List<WordDefinition> {
        return databaseManager?.getWordsByType(type) ?: emptyList()
    }
    
    /**
     * 按难度获取词汇
     */
    fun getWordsByDifficulty(difficulty: String): List<WordDefinition> {
        return databaseManager?.getWordsByDifficulty(difficulty) ?: emptyList()
    }
    
    /**
     * 获取首页词汇（中考词汇和超纲词汇）
     */
    fun getHomePageWords(): List<WordDefinition> {
        return databaseManager?.getHomePageWords() ?: emptyList()
    }
    
    /**
     * 搜索词汇
     */
    fun searchWords(keyword: String): List<WordDefinition> {
        return databaseManager?.searchWords(keyword) ?: emptyList()
    }
    
    /**
     * 获取所有类别
     */
    fun getAllCategories(): List<String> {
        return databaseManager?.getAllCategories() ?: emptyList()
    }
    
    /**
     * 获取所有难度级别
     */
    fun getAllDifficulties(): List<String> {
        return databaseManager?.getAllDifficulties() ?: emptyList()
    }
    
    /**
     * 获取词汇总数
     */
    fun getWordCount(): Int {
        return databaseManager?.getWordCount() ?: 0
    }
    
    /**
     * 检查词汇是否存在
     */
    fun isWordExists(word: String): Boolean {
        return databaseManager?.isWordExists(word) ?: false
    }
    
    /**
     * 关闭数据库
     */
    fun close() {
        databaseManager?.closeDatabase()
    }
}


