package com.vocabulary.scanner.data;

/**
 * SQLite词汇数据库助手
 * 从assets目录加载预制的1689个中考英语核心词汇数据库
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0002J\b\u0010\u0007\u001a\u00020\u0006H\u0002J\u0010\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\nH\u0016J \u0010\u000b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/vocabulary/scanner/data/VocabularyDatabaseHelper;", "Landroid/database/sqlite/SQLiteOpenHelper;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "copyDatabaseFromAssets", "", "createEmptyTable", "onCreate", "db", "Landroid/database/sqlite/SQLiteDatabase;", "onUpgrade", "oldVersion", "", "newVersion", "Companion", "app_debug"})
public final class VocabularyDatabaseHelper extends android.database.sqlite.SQLiteOpenHelper {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DATABASE_NAME = "vocabulary.db";
    private static final int DATABASE_VERSION = 4;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TABLE_VOCABULARY = "vocabulary";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COLUMN_ID = "id";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COLUMN_WORD = "word";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COLUMN_MEANING = "meaning";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COLUMN_CATEGORY = "category";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COLUMN_DIFFICULTY = "difficulty";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COLUMN_TYPE = "type";
    @org.jetbrains.annotations.NotNull()
    public static final com.vocabulary.scanner.data.VocabularyDatabaseHelper.Companion Companion = null;
    
    public VocabularyDatabaseHelper(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null, null, null, 0);
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.NotNull()
    android.database.sqlite.SQLiteDatabase db) {
    }
    
    @java.lang.Override()
    public void onUpgrade(@org.jetbrains.annotations.NotNull()
    android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    /**
     * 从assets目录复制预制的数据库文件
     */
    private final void copyDatabaseFromAssets() {
    }
    
    /**
     * 创建空表作为备用方案
     */
    private final void createEmptyTable() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/vocabulary/scanner/data/VocabularyDatabaseHelper$Companion;", "", "()V", "COLUMN_CATEGORY", "", "COLUMN_DIFFICULTY", "COLUMN_ID", "COLUMN_MEANING", "COLUMN_TYPE", "COLUMN_WORD", "DATABASE_NAME", "DATABASE_VERSION", "", "TABLE_VOCABULARY", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}