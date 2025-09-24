package com.vocabulary.scanner.ui.vocabulary;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010#\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000f\u001a\u00020\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u0010H\u0002J\u0012\u0010\u0012\u001a\u00020\u00102\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0014J\u0010\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\tH\u0002J\b\u0010\u0017\u001a\u00020\u0010H\u0002J\b\u0010\u0018\u001a\u00020\u0010H\u0002J\b\u0010\u0019\u001a\u00020\u0010H\u0002J\b\u0010\u001a\u001a\u00020\u0010H\u0002J\b\u0010\u001b\u001a\u00020\u0010H\u0002J\b\u0010\u001c\u001a\u00020\u0010H\u0002J\u0010\u0010\u001d\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\tH\u0002J\b\u0010\u001e\u001a\u00020\u0010H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/vocabulary/scanner/ui/vocabulary/VocabularyActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/vocabulary/scanner/databinding/ActivityVocabularyBinding;", "isBatchMode", "", "selectedItems", "", "", "vocabularyAdapter", "Lcom/vocabulary/scanner/ui/vocabulary/VocabularyAdapter;", "vocabularyList", "", "Lcom/vocabulary/scanner/ui/vocabulary/VocabularyListItem;", "deleteSelectedItems", "", "loadVocabularyData", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "openWordDetail", "position", "setupClickListeners", "setupRecyclerView", "sortByAddTime", "sortByAlphabet", "startReview", "toggleBatchMode", "toggleItemSelection", "updateDeleteButtonText", "app_debug"})
public final class VocabularyActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.vocabulary.scanner.databinding.ActivityVocabularyBinding binding;
    private com.vocabulary.scanner.ui.vocabulary.VocabularyAdapter vocabularyAdapter;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.vocabulary.scanner.ui.vocabulary.VocabularyListItem> vocabularyList;
    private boolean isBatchMode = false;
    @org.jetbrains.annotations.NotNull()
    private java.util.Set<java.lang.Integer> selectedItems;
    
    public VocabularyActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void loadVocabularyData() {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void toggleBatchMode() {
    }
    
    private final void toggleItemSelection(int position) {
    }
    
    private final void updateDeleteButtonText() {
    }
    
    private final void openWordDetail(int position) {
    }
    
    private final void sortByAddTime() {
    }
    
    private final void sortByAlphabet() {
    }
    
    private final void startReview() {
    }
    
    private final void deleteSelectedItems() {
    }
}