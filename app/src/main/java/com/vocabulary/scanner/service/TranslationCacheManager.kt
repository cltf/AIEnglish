package com.vocabulary.scanner.service

import android.content.Context
import android.content.SharedPreferences
import android.util.LruCache
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

/**
 * 翻译缓存管理器
 * 提供内存缓存和持久化缓存功能，提高翻译性能
 */
class TranslationCacheManager(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "translation_cache"
        private const val MAX_CACHE_SIZE = 1000 // 最大缓存条目数
        private const val CACHE_EXPIRY_TIME = 7 * 24 * 60 * 60 * 1000L // 7天过期时间
    }
    
    // 内存缓存
    private val memoryCache = LruCache<String, CachedTranslation>(MAX_CACHE_SIZE)
    
    // 持久化缓存
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // 线程安全的缓存操作
    private val cacheLock = Any()
    
    /**
     * 获取缓存的翻译结果
     */
    fun getCachedTranslation(
        text: String,
        from: String,
        to: String,
        provider: TranslationService.TranslationProvider
    ): TranslationResult? {
        val cacheKey = generateCacheKey(text, from, to, provider)
        
        synchronized(cacheLock) {
            // 先从内存缓存获取
            val memoryResult = memoryCache.get(cacheKey)
            if (memoryResult != null && !memoryResult.isExpired()) {
                return memoryResult.translationResult
            }
            
            // 从持久化缓存获取
            val persistentResult = getFromPersistentCache(cacheKey)
            if (persistentResult != null && !persistentResult.isExpired()) {
                // 重新加载到内存缓存
                memoryCache.put(cacheKey, persistentResult)
                return persistentResult.translationResult
            }
        }
        
        return null
    }
    
    /**
     * 缓存翻译结果
     */
    fun cacheTranslation(
        text: String,
        from: String,
        to: String,
        provider: TranslationService.TranslationProvider,
        result: TranslationResult
    ) {
        val cacheKey = generateCacheKey(text, from, to, provider)
        val cachedTranslation = CachedTranslation(
            translationResult = result,
            timestamp = System.currentTimeMillis()
        )
        
        synchronized(cacheLock) {
            // 存储到内存缓存
            memoryCache.put(cacheKey, cachedTranslation)
            
            // 存储到持久化缓存
            saveToPersistentCache(cacheKey, cachedTranslation)
        }
    }
    
    /**
     * 清除过期缓存
     */
    fun clearExpiredCache() {
        synchronized(cacheLock) {
            // 清除内存缓存中的过期条目
            val memorySnapshot = memoryCache.snapshot()
            for ((key, value) in memorySnapshot) {
                if (value.isExpired()) {
                    memoryCache.remove(key)
                }
            }
            
            // 清除持久化缓存中的过期条目
            val allEntries = sharedPreferences.all
            val editor = sharedPreferences.edit()
            
            for ((key, value) in allEntries) {
                if (key.startsWith("cache_")) {
                    try {
                        val json = JSONObject(value as String)
                        val timestamp = json.getLong("timestamp")
                        if (System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME) {
                            editor.remove(key)
                        }
                    } catch (e: Exception) {
                        // 如果解析失败，删除该条目
                        editor.remove(key)
                    }
                }
            }
            
            editor.apply()
        }
    }
    
    /**
     * 清除所有缓存
     */
    fun clearAllCache() {
        synchronized(cacheLock) {
            memoryCache.evictAll()
            sharedPreferences.edit().clear().apply()
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getCacheStats(): CacheStats {
        synchronized(cacheLock) {
            val memorySize = memoryCache.size()
            val memoryHitCount = memoryCache.hitCount()
            val memoryMissCount = memoryCache.missCount()
            
            val persistentSize = sharedPreferences.all.count { (key, _) ->
                key.startsWith("cache_")
            }
            
            return CacheStats(
                memoryCacheSize = memorySize,
                memoryHitCount = memoryHitCount,
                memoryMissCount = memoryMissCount,
                persistentCacheSize = persistentSize
            )
        }
    }
    
    /**
     * 生成缓存键
     */
    private fun generateCacheKey(
        text: String,
        from: String,
        to: String,
        provider: TranslationService.TranslationProvider
    ): String {
        return "${provider.name}_${from}_${to}_${text.hashCode()}"
    }
    
    /**
     * 从持久化缓存获取
     */
    private fun getFromPersistentCache(cacheKey: String): CachedTranslation? {
        val jsonString = sharedPreferences.getString("cache_$cacheKey", null) ?: return null
        
        return try {
            val json = JSONObject(jsonString)
            val translationResult = TranslationResult(
                success = json.getBoolean("success"),
                originalText = json.getString("originalText"),
                translatedText = json.getString("translatedText"),
                provider = json.optString("provider", ""),
                error = json.optString("error", null)
            )
            
            CachedTranslation(
                translationResult = translationResult,
                timestamp = json.getLong("timestamp")
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 保存到持久化缓存
     */
    private fun saveToPersistentCache(cacheKey: String, cachedTranslation: CachedTranslation) {
        val json = JSONObject().apply {
            put("success", cachedTranslation.translationResult.success)
            put("originalText", cachedTranslation.translationResult.originalText)
            put("translatedText", cachedTranslation.translationResult.translatedText)
            put("provider", cachedTranslation.translationResult.provider)
            put("error", cachedTranslation.translationResult.error ?: "")
            put("timestamp", cachedTranslation.timestamp)
        }
        
        sharedPreferences.edit()
            .putString("cache_$cacheKey", json.toString())
            .apply()
    }
    
    /**
     * 缓存的翻译结果
     */
    private data class CachedTranslation(
        val translationResult: TranslationResult,
        val timestamp: Long
    ) {
        fun isExpired(): Boolean {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME
        }
    }
    
    /**
     * 缓存统计信息
     */
    data class CacheStats(
        val memoryCacheSize: Int,
        val memoryHitCount: Long,
        val memoryMissCount: Long,
        val persistentCacheSize: Int
    ) {
        val memoryHitRate: Double
            get() = if (memoryHitCount + memoryMissCount > 0) {
                memoryHitCount.toDouble() / (memoryHitCount + memoryMissCount)
            } else 0.0
    }
}
