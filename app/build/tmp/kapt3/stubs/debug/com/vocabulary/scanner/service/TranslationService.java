package com.vocabulary.scanner.service;

/**
 * 翻译服务类
 * 支持多种翻译API：百度翻译、Google翻译、有道翻译等
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u0000 02\u00020\u0001:\u00010B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J$\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u000bH\u0002J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\rJ\u0018\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\bH\u0002J \u0010\u0012\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\bH\u0002J\u0006\u0010\u0014\u001a\u00020\u0015J\u0014\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u000bH\u0002J*\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\b2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u000bH\u0082@\u00a2\u0006\u0002\u0010\u0019J\u0010\u0010\u001a\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\bH\u0002J\u0018\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020\bH\u0002J\u0018\u0010 \u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020\bH\u0002J\u0018\u0010!\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020\bH\u0002J\u0010\u0010\"\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\bH\u0002J4\u0010#\u001a\u00020\u001d2\u0006\u0010\u0010\u001a\u00020\b2\b\b\u0002\u0010$\u001a\u00020\b2\b\b\u0002\u0010%\u001a\u00020\b2\b\b\u0002\u0010&\u001a\u00020\'H\u0086@\u00a2\u0006\u0002\u0010(J&\u0010)\u001a\u00020\u001d2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010$\u001a\u00020\b2\u0006\u0010%\u001a\u00020\bH\u0082@\u00a2\u0006\u0002\u0010*J&\u0010+\u001a\u00020\u001d2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010$\u001a\u00020\b2\u0006\u0010%\u001a\u00020\bH\u0082@\u00a2\u0006\u0002\u0010*J&\u0010,\u001a\u00020\u001d2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010$\u001a\u00020\b2\u0006\u0010%\u001a\u00020\bH\u0082@\u00a2\u0006\u0002\u0010*J$\u0010-\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\b2\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u000bH\u0002J&\u0010/\u001a\u00020\u001d2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010$\u001a\u00020\b2\u0006\u0010%\u001a\u00020\bH\u0082@\u00a2\u0006\u0002\u0010*R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00061"}, d2 = {"Lcom/vocabulary/scanner/service/TranslationService;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "cacheManager", "Lcom/vocabulary/scanner/service/TranslationCacheManager;", "buildUrlWithParams", "", "baseUrl", "params", "", "clearCache", "", "clearExpiredCache", "generateBaiduSign", "text", "salt", "generateYoudaoSign", "curtime", "getCacheStats", "Lcom/vocabulary/scanner/service/TranslationCacheManager$CacheStats;", "getLocalTranslations", "makeHttpRequest", "url", "(Ljava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "md5", "input", "parseBaiduResponse", "Lcom/vocabulary/scanner/service/TranslationResult;", "response", "originalText", "parseGoogleResponse", "parseYoudaoResponse", "sha256", "translate", "from", "to", "provider", "Lcom/vocabulary/scanner/service/TranslationProvider;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/vocabulary/scanner/service/TranslationProvider;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "translateWithBaidu", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "translateWithGoogle", "translateWithLocal", "translateWithLocalDictionary", "dictionary", "translateWithYoudao", "Companion", "app_debug"})
public final class TranslationService {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vocabulary.scanner.service.TranslationCacheManager cacheManager = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "TranslationService";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BAIDU_APP_ID = "your_baidu_app_id";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BAIDU_SECRET_KEY = "your_baidu_secret_key";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BAIDU_API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String GOOGLE_API_KEY = "your_google_api_key";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String GOOGLE_API_URL = "https://translation.googleapis.com/language/translate/v2";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String YOUDAO_APP_KEY = "your_youdao_app_key";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String YOUDAO_APP_SECRET = "your_youdao_app_secret";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String YOUDAO_API_URL = "https://openapi.youdao.com/api";
    @org.jetbrains.annotations.NotNull()
    public static final com.vocabulary.scanner.service.TranslationService.Companion Companion = null;
    
    public TranslationService(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    /**
     * 翻译文本
     * @param text 要翻译的文本
     * @param from 源语言
     * @param to 目标语言
     * @param provider 翻译服务提供商
     * @return 翻译结果
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object translate(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    java.lang.String from, @org.jetbrains.annotations.NotNull()
    java.lang.String to, @org.jetbrains.annotations.NotNull()
    com.vocabulary.scanner.service.TranslationProvider provider, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.vocabulary.scanner.service.TranslationResult> $completion) {
        return null;
    }
    
    /**
     * 使用百度翻译API
     */
    private final java.lang.Object translateWithBaidu(java.lang.String text, java.lang.String from, java.lang.String to, kotlin.coroutines.Continuation<? super com.vocabulary.scanner.service.TranslationResult> $completion) {
        return null;
    }
    
    /**
     * 使用Google翻译API
     */
    private final java.lang.Object translateWithGoogle(java.lang.String text, java.lang.String from, java.lang.String to, kotlin.coroutines.Continuation<? super com.vocabulary.scanner.service.TranslationResult> $completion) {
        return null;
    }
    
    /**
     * 使用有道翻译API
     */
    private final java.lang.Object translateWithYoudao(java.lang.String text, java.lang.String from, java.lang.String to, kotlin.coroutines.Continuation<? super com.vocabulary.scanner.service.TranslationResult> $completion) {
        return null;
    }
    
    /**
     * 使用本地词典翻译
     */
    private final java.lang.Object translateWithLocal(java.lang.String text, java.lang.String from, java.lang.String to, kotlin.coroutines.Continuation<? super com.vocabulary.scanner.service.TranslationResult> $completion) {
        return null;
    }
    
    /**
     * 生成百度翻译签名
     */
    private final java.lang.String generateBaiduSign(java.lang.String text, java.lang.String salt) {
        return null;
    }
    
    /**
     * 生成有道翻译签名
     */
    private final java.lang.String generateYoudaoSign(java.lang.String text, java.lang.String salt, java.lang.String curtime) {
        return null;
    }
    
    /**
     * 发送HTTP请求
     */
    private final java.lang.Object makeHttpRequest(java.lang.String url, java.util.Map<java.lang.String, java.lang.String> params, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    /**
     * 构建带参数的URL
     */
    private final java.lang.String buildUrlWithParams(java.lang.String baseUrl, java.util.Map<java.lang.String, java.lang.String> params) {
        return null;
    }
    
    /**
     * 解析百度翻译响应
     */
    private final com.vocabulary.scanner.service.TranslationResult parseBaiduResponse(java.lang.String response, java.lang.String originalText) {
        return null;
    }
    
    /**
     * 解析Google翻译响应
     */
    private final com.vocabulary.scanner.service.TranslationResult parseGoogleResponse(java.lang.String response, java.lang.String originalText) {
        return null;
    }
    
    /**
     * 解析有道翻译响应
     */
    private final com.vocabulary.scanner.service.TranslationResult parseYoudaoResponse(java.lang.String response, java.lang.String originalText) {
        return null;
    }
    
    /**
     * 获取本地翻译词典
     */
    private final java.util.Map<java.lang.String, java.lang.String> getLocalTranslations() {
        return null;
    }
    
    /**
     * 使用本地词典翻译
     */
    private final java.lang.String translateWithLocalDictionary(java.lang.String text, java.util.Map<java.lang.String, java.lang.String> dictionary) {
        return null;
    }
    
    /**
     * MD5加密
     */
    private final java.lang.String md5(java.lang.String input) {
        return null;
    }
    
    /**
     * SHA256加密
     */
    private final java.lang.String sha256(java.lang.String input) {
        return null;
    }
    
    /**
     * 清除翻译缓存
     */
    public final void clearCache() {
    }
    
    /**
     * 清除过期缓存
     */
    public final void clearExpiredCache() {
    }
    
    /**
     * 获取缓存统计信息
     */
    @org.jetbrains.annotations.NotNull()
    public final com.vocabulary.scanner.service.TranslationCacheManager.CacheStats getCacheStats() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/vocabulary/scanner/service/TranslationService$Companion;", "", "()V", "BAIDU_API_URL", "", "BAIDU_APP_ID", "BAIDU_SECRET_KEY", "GOOGLE_API_KEY", "GOOGLE_API_URL", "TAG", "YOUDAO_API_URL", "YOUDAO_APP_KEY", "YOUDAO_APP_SECRET", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}