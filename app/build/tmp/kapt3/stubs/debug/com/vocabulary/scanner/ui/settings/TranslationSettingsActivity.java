package com.vocabulary.scanner.ui.settings;

/**
 * 翻译设置页面
 * 允许用户选择翻译服务提供商和配置API密钥
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0002J\b\u0010\t\u001a\u00020\nH\u0002J\u0012\u0010\u000b\u001a\u00020\n2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\b\u0010\u000e\u001a\u00020\nH\u0002J\b\u0010\u000f\u001a\u00020\nH\u0002J\b\u0010\u0010\u001a\u00020\nH\u0002J\u0010\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/vocabulary/scanner/ui/settings/TranslationSettingsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/vocabulary/scanner/databinding/ActivityTranslationSettingsBinding;", "sharedPreferences", "Landroid/content/SharedPreferences;", "getCurrentProvider", "Lcom/vocabulary/scanner/service/TranslationProvider;", "loadSettings", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "saveSettings", "setupClickListeners", "testTranslation", "updateProviderSelection", "provider", "Companion", "app_debug"})
public final class TranslationSettingsActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.vocabulary.scanner.databinding.ActivityTranslationSettingsBinding binding;
    private android.content.SharedPreferences sharedPreferences;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "translation_settings";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_TRANSLATION_PROVIDER = "translation_provider";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_BAIDU_APP_ID = "baidu_app_id";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_BAIDU_SECRET_KEY = "baidu_secret_key";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_GOOGLE_API_KEY = "google_api_key";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_YOUDAO_APP_KEY = "youdao_app_key";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_YOUDAO_APP_SECRET = "youdao_app_secret";
    @org.jetbrains.annotations.NotNull()
    public static final com.vocabulary.scanner.ui.settings.TranslationSettingsActivity.Companion Companion = null;
    
    public TranslationSettingsActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void loadSettings() {
    }
    
    private final void saveSettings() {
    }
    
    private final void testTranslation() {
    }
    
    private final com.vocabulary.scanner.service.TranslationProvider getCurrentProvider() {
        return null;
    }
    
    private final void updateProviderSelection(com.vocabulary.scanner.service.TranslationProvider provider) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/vocabulary/scanner/ui/settings/TranslationSettingsActivity$Companion;", "", "()V", "KEY_BAIDU_APP_ID", "", "KEY_BAIDU_SECRET_KEY", "KEY_GOOGLE_API_KEY", "KEY_TRANSLATION_PROVIDER", "KEY_YOUDAO_APP_KEY", "KEY_YOUDAO_APP_SECRET", "PREFS_NAME", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}