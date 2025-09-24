package com.vocabulary.scanner

import android.app.Application
import com.vocabulary.scanner.data.UnifiedVocabularyDatabase

/**
 * 应用程序类
 * 负责初始化全局资源
 */
class VocabularyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 延迟数据库初始化，避免启动时的权限问题
        // UnifiedVocabularyDatabase.initialize(this)
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        // 关闭数据库
        UnifiedVocabularyDatabase.close()
    }
}




