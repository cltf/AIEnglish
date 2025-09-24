package com.vocabulary.scanner.service;

/**
 * 翻译缓存管理器
 * 提供内存缓存和持久化缓存功能，提高翻译性能
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 !2\u00020\u0001:\u0003\u001f !B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J.\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\u0006\u0010\u0015\u001a\u00020\rJ\u0006\u0010\u0016\u001a\u00020\rJ(\u0010\u0017\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0006\u0010\u0018\u001a\u00020\u0019J(\u0010\u001a\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u0012J\u0012\u0010\u001b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u001c\u001a\u00020\bH\u0002J\u0018\u0010\u001d\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\b2\u0006\u0010\u001e\u001a\u00020\tH\u0002R\u000e\u0010\u0005\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/vocabulary/scanner/service/TranslationCacheManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "cacheLock", "memoryCache", "Landroid/util/LruCache;", "", "Lcom/vocabulary/scanner/service/TranslationCacheManager$CachedTranslation;", "sharedPreferences", "Landroid/content/SharedPreferences;", "cacheTranslation", "", "text", "from", "to", "provider", "Lcom/vocabulary/scanner/service/TranslationProvider;", "result", "Lcom/vocabulary/scanner/service/TranslationResult;", "clearAllCache", "clearExpiredCache", "generateCacheKey", "getCacheStats", "Lcom/vocabulary/scanner/service/TranslationCacheManager$CacheStats;", "getCachedTranslation", "getFromPersistentCache", "cacheKey", "saveToPersistentCache", "cachedTranslation", "CacheStats", "CachedTranslation", "Companion", "app_debug"})
public final class TranslationCacheManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "translation_cache";
    private static final int MAX_CACHE_SIZE = 1000;
    private static final long CACHE_EXPIRY_TIME = 604800000L;
    @org.jetbrains.annotations.NotNull()
    private final android.util.LruCache<java.lang.String, com.vocabulary.scanner.service.TranslationCacheManager.CachedTranslation> memoryCache = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.SharedPreferences sharedPreferences = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Object cacheLock = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.vocabulary.scanner.service.TranslationCacheManager.Companion Companion = null;
    
    public TranslationCacheManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    /**
     * 获取缓存的翻译结果
     */
    @org.jetbrains.annotations.Nullable()
    public final com.vocabulary.scanner.service.TranslationResult getCachedTranslation(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String from, @org.jetbrains.annotations.NotNull()
    java.lang.String to, @org.jetbrains.annotations.NotNull()
    com.vocabulary.scanner.service.TranslationProvider provider) {
        return null;
    }
    
    /**
     * 缓存翻译结果
     */
    public final void cacheTranslation(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String from, @org.jetbrains.annotations.NotNull()
    java.lang.String to, @org.jetbrains.annotations.NotNull()
    com.vocabulary.scanner.service.TranslationProvider provider, @org.jetbrains.annotations.NotNull()
    com.vocabulary.scanner.service.TranslationResult result) {
    }
    
    /**
     * 清除过期缓存
     */
    public final void clearExpiredCache() {
    }
    
    /**
     * 清除所有缓存
     */
    public final void clearAllCache() {
    }
    
    /**
     * 获取缓存统计信息
     */
    @org.jetbrains.annotations.NotNull()
    public final com.vocabulary.scanner.service.TranslationCacheManager.CacheStats getCacheStats() {
        return null;
    }
    
    /**
     * 生成缓存键
     */
    private final java.lang.String generateCacheKey(java.lang.String text, java.lang.String from, java.lang.String to, com.vocabulary.scanner.service.TranslationProvider provider) {
        return null;
    }
    
    /**
     * 从持久化缓存获取
     */
    private final com.vocabulary.scanner.service.TranslationCacheManager.CachedTranslation getFromPersistentCache(java.lang.String cacheKey) {
        return null;
    }
    
    /**
     * 保存到持久化缓存
     */
    private final void saveToPersistentCache(java.lang.String cacheKey, com.vocabulary.scanner.service.TranslationCacheManager.CachedTranslation cachedTranslation) {
    }
    
    /**
     * 缓存统计信息
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\bJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e8F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\fR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\n\u00a8\u0006\u001e"}, d2 = {"Lcom/vocabulary/scanner/service/TranslationCacheManager$CacheStats;", "", "memoryCacheSize", "", "memoryHitCount", "", "memoryMissCount", "persistentCacheSize", "(IJJI)V", "getMemoryCacheSize", "()I", "getMemoryHitCount", "()J", "memoryHitRate", "", "getMemoryHitRate", "()D", "getMemoryMissCount", "getPersistentCacheSize", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    public static final class CacheStats {
        private final int memoryCacheSize = 0;
        private final long memoryHitCount = 0L;
        private final long memoryMissCount = 0L;
        private final int persistentCacheSize = 0;
        
        public CacheStats(int memoryCacheSize, long memoryHitCount, long memoryMissCount, int persistentCacheSize) {
            super();
        }
        
        public final int getMemoryCacheSize() {
            return 0;
        }
        
        public final long getMemoryHitCount() {
            return 0L;
        }
        
        public final long getMemoryMissCount() {
            return 0L;
        }
        
        public final int getPersistentCacheSize() {
            return 0;
        }
        
        public final double getMemoryHitRate() {
            return 0.0;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final long component2() {
            return 0L;
        }
        
        public final long component3() {
            return 0L;
        }
        
        public final int component4() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.vocabulary.scanner.service.TranslationCacheManager.CacheStats copy(int memoryCacheSize, long memoryHitCount, long memoryMissCount, int persistentCacheSize) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    /**
     * 缓存的翻译结果
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001J\u0006\u0010\u0013\u001a\u00020\u000fJ\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0016"}, d2 = {"Lcom/vocabulary/scanner/service/TranslationCacheManager$CachedTranslation;", "", "translationResult", "Lcom/vocabulary/scanner/service/TranslationResult;", "timestamp", "", "(Lcom/vocabulary/scanner/service/TranslationResult;J)V", "getTimestamp", "()J", "getTranslationResult", "()Lcom/vocabulary/scanner/service/TranslationResult;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "isExpired", "toString", "", "app_debug"})
    static final class CachedTranslation {
        @org.jetbrains.annotations.NotNull()
        private final com.vocabulary.scanner.service.TranslationResult translationResult = null;
        private final long timestamp = 0L;
        
        public CachedTranslation(@org.jetbrains.annotations.NotNull()
        com.vocabulary.scanner.service.TranslationResult translationResult, long timestamp) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.vocabulary.scanner.service.TranslationResult getTranslationResult() {
            return null;
        }
        
        public final long getTimestamp() {
            return 0L;
        }
        
        public final boolean isExpired() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.vocabulary.scanner.service.TranslationResult component1() {
            return null;
        }
        
        public final long component2() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.vocabulary.scanner.service.TranslationCacheManager.CachedTranslation copy(@org.jetbrains.annotations.NotNull()
        com.vocabulary.scanner.service.TranslationResult translationResult, long timestamp) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/vocabulary/scanner/service/TranslationCacheManager$Companion;", "", "()V", "CACHE_EXPIRY_TIME", "", "MAX_CACHE_SIZE", "", "PREFS_NAME", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}