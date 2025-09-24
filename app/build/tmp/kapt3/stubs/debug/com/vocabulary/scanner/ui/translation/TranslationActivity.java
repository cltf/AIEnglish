package com.vocabulary.scanner.ui.translation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0002J\u0010\u0010\u000e\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0002J\b\u0010\u000f\u001a\u00020\fH\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u0012\u0010\u0012\u001a\u00020\f2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0014J\b\u0010\u0015\u001a\u00020\fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/vocabulary/scanner/ui/translation/TranslationActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/vocabulary/scanner/databinding/ActivityTranslationBinding;", "isTranslationMode", "", "originalText", "", "translationService", "Lcom/vocabulary/scanner/service/TranslationService;", "addEnglishOnly", "", "englishText", "addTranslationPair", "generateTranslationContent", "getSelectedTranslationProvider", "Lcom/vocabulary/scanner/service/TranslationProvider;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupClickListeners", "Companion", "app_debug"})
public final class TranslationActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.vocabulary.scanner.databinding.ActivityTranslationBinding binding;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String originalText = "";
    private boolean isTranslationMode = true;
    private com.vocabulary.scanner.service.TranslationService translationService;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "translation_settings";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_TRANSLATION_PROVIDER = "translation_provider";
    @org.jetbrains.annotations.NotNull()
    public static final com.vocabulary.scanner.ui.translation.TranslationActivity.Companion Companion = null;
    
    public TranslationActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void generateTranslationContent() {
    }
    
    private final void addTranslationPair(java.lang.String englishText) {
    }
    
    private final void addEnglishOnly(java.lang.String englishText) {
    }
    
    private final com.vocabulary.scanner.service.TranslationProvider getSelectedTranslationProvider() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/vocabulary/scanner/ui/translation/TranslationActivity$Companion;", "", "()V", "KEY_TRANSLATION_PROVIDER", "", "PREFS_NAME", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}