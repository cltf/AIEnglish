package com.vocabulary.scanner.service;

/**
 * 翻译服务提供商枚举
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/vocabulary/scanner/service/TranslationProvider;", "", "(Ljava/lang/String;I)V", "BAIDU", "GOOGLE", "YOUDAO", "LOCAL", "app_debug"})
public enum TranslationProvider {
    /*public static final*/ BAIDU /* = new BAIDU() */,
    /*public static final*/ GOOGLE /* = new GOOGLE() */,
    /*public static final*/ YOUDAO /* = new YOUDAO() */,
    /*public static final*/ LOCAL /* = new LOCAL() */;
    
    TranslationProvider() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.vocabulary.scanner.service.TranslationProvider> getEntries() {
        return null;
    }
}