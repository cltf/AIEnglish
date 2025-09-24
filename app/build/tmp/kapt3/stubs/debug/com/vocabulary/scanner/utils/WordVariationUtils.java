package com.vocabulary.scanner.utils;

/**
 * 单词变形处理工具类
 * 处理时态、单复数、人称等因素导致的单词变形
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0007J\u0012\u0010\b\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002J\u0012\u0010\t\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002J\u0012\u0010\n\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002J\u0012\u0010\u000b\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002J\u0012\u0010\f\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a8\u0006\r"}, d2 = {"Lcom/vocabulary/scanner/utils/WordVariationUtils;", "", "()V", "findWordInVocabulary", "", "word", "vocabularySet", "", "getAdjectiveBaseForm", "getIrregularBaseForm", "getPossessiveBaseForm", "getSingularForm", "getVerbBaseForm", "app_debug"})
public final class WordVariationUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.vocabulary.scanner.utils.WordVariationUtils INSTANCE = null;
    
    private WordVariationUtils() {
        super();
    }
    
    /**
     * 检查单词是否在词汇库中（考虑变形）
     * @param word 要检查的单词
     * @param vocabularySet 词汇库
     * @return 如果找到匹配的词汇则返回原形，否则返回null
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String findWordInVocabulary(@org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> vocabularySet) {
        return null;
    }
    
    /**
     * 获取名词的单数形式
     */
    private final java.lang.String getSingularForm(java.lang.String word) {
        return null;
    }
    
    /**
     * 获取动词的原形
     */
    private final java.lang.String getVerbBaseForm(java.lang.String word) {
        return null;
    }
    
    /**
     * 获取形容词的原形
     */
    private final java.lang.String getAdjectiveBaseForm(java.lang.String word) {
        return null;
    }
    
    /**
     * 处理名词所有格
     */
    private final java.lang.String getPossessiveBaseForm(java.lang.String word) {
        return null;
    }
    
    /**
     * 处理不规则变形
     */
    private final java.lang.String getIrregularBaseForm(java.lang.String word) {
        return null;
    }
}