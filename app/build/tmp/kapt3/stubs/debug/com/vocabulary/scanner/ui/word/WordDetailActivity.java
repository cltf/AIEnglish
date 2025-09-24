package com.vocabulary.scanner.ui.word;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0006H\u0002J\u0012\u0010\u0011\u001a\u00020\u000f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0014J\b\u0010\u0014\u001a\u00020\u000fH\u0002J\b\u0010\u0015\u001a\u00020\u000fH\u0002J\b\u0010\u0016\u001a\u00020\u000fH\u0002J\b\u0010\u0017\u001a\u00020\u000fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\fX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\r\u00a8\u0006\u0018"}, d2 = {"Lcom/vocabulary/scanner/ui/word/WordDetailActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/vocabulary/scanner/databinding/ActivityWordDetailBinding;", "currentWord", "", "currentWordIndex", "", "isAddedToVocabulary", "", "unknownWords", "", "[Ljava/lang/String;", "loadWordDefinition", "", "word", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupClickListeners", "setupWordDetail", "toggleAddToVocabulary", "updateNavigationButtons", "app_debug"})
public final class WordDetailActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.vocabulary.scanner.databinding.ActivityWordDetailBinding binding;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String currentWord = "";
    @org.jetbrains.annotations.NotNull()
    private java.lang.String[] unknownWords = {};
    private int currentWordIndex = 0;
    private boolean isAddedToVocabulary = false;
    
    public WordDetailActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupWordDetail() {
    }
    
    private final void loadWordDefinition(java.lang.String word) {
    }
    
    private final void updateNavigationButtons() {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void toggleAddToVocabulary() {
    }
}