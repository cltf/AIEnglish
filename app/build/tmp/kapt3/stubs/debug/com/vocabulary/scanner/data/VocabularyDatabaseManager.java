package com.vocabulary.scanner.data;

/**
 * 词汇数据库管理器
 * 提供词汇的增删改查功能
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\t\u001a\u00020\nJ\b\u0010\u000b\u001a\u00020\nH\u0002J\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rJ\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rJ\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\rJ\b\u0010\u0012\u001a\u0004\u0018\u00010\u0006J\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0014\u001a\u00020\u000eJ\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00110\rJ\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0017J\u0006\u0010\u0018\u001a\u00020\u0019J\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00110\r2\u0006\u0010\u001b\u001a\u00020\u000eJ\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00110\r2\u0006\u0010\u001d\u001a\u00020\u000eJ\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00110\r2\u0006\u0010\u001f\u001a\u00020 J\u000e\u0010!\u001a\u00020\"2\u0006\u0010\u0014\u001a\u00020\u000eJ\u0006\u0010#\u001a\u00020\nJ\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00110\r2\u0006\u0010%\u001a\u00020\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/vocabulary/scanner/data/VocabularyDatabaseManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "database", "Landroid/database/sqlite/SQLiteDatabase;", "dbHelper", "Lcom/vocabulary/scanner/data/VocabularyDatabaseHelper;", "closeDatabase", "", "copyDataFromAssets", "getAllCategories", "", "", "getAllDifficulties", "getAllWords", "Lcom/vocabulary/scanner/data/WordDefinition;", "getDatabase", "getDefinition", "word", "getHomePageWords", "getVocabularySet", "", "getWordCount", "", "getWordsByCategory", "category", "getWordsByDifficulty", "difficulty", "getWordsByType", "type", "Lcom/vocabulary/scanner/data/VocabularyType;", "isWordExists", "", "openDatabase", "searchWords", "keyword", "app_debug"})
public final class VocabularyDatabaseManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.vocabulary.scanner.data.VocabularyDatabaseHelper dbHelper = null;
    @org.jetbrains.annotations.Nullable()
    private android.database.sqlite.SQLiteDatabase database;
    
    public VocabularyDatabaseManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    /**
     * 打开数据库
     */
    public final void openDatabase() {
    }
    
    /**
     * 从assets目录复制数据
     */
    private final void copyDataFromAssets() {
    }
    
    /**
     * 关闭数据库
     */
    public final void closeDatabase() {
    }
    
    /**
     * 获取数据库实例（用于直接操作）
     */
    @org.jetbrains.annotations.Nullable()
    public final android.database.sqlite.SQLiteDatabase getDatabase() {
        return null;
    }
    
    /**
     * 获取所有词汇
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vocabulary.scanner.data.WordDefinition> getAllWords() {
        return null;
    }
    
    /**
     * 根据单词查询释义
     */
    @org.jetbrains.annotations.Nullable()
    public final com.vocabulary.scanner.data.WordDefinition getDefinition(@org.jetbrains.annotations.NotNull()
    java.lang.String word) {
        return null;
    }
    
    /**
     * 获取词汇集合
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getVocabularySet() {
        return null;
    }
    
    /**
     * 按类别查询词汇
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vocabulary.scanner.data.WordDefinition> getWordsByCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String category) {
        return null;
    }
    
    /**
     * 按词汇类型查询词汇
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vocabulary.scanner.data.WordDefinition> getWordsByType(@org.jetbrains.annotations.NotNull()
    com.vocabulary.scanner.data.VocabularyType type) {
        return null;
    }
    
    /**
     * 按难度查询词汇
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vocabulary.scanner.data.WordDefinition> getWordsByDifficulty(@org.jetbrains.annotations.NotNull()
    java.lang.String difficulty) {
        return null;
    }
    
    /**
     * 获取首页词汇（中考词汇和超纲词汇）
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vocabulary.scanner.data.WordDefinition> getHomePageWords() {
        return null;
    }
    
    /**
     * 搜索词汇
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.vocabulary.scanner.data.WordDefinition> searchWords(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword) {
        return null;
    }
    
    /**
     * 获取所有类别
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getAllCategories() {
        return null;
    }
    
    /**
     * 获取所有难度级别
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getAllDifficulties() {
        return null;
    }
    
    /**
     * 获取词汇总数
     */
    public final int getWordCount() {
        return 0;
    }
    
    /**
     * 检查词汇是否存在
     */
    public final boolean isWordExists(@org.jetbrains.annotations.NotNull()
    java.lang.String word) {
        return false;
    }
}