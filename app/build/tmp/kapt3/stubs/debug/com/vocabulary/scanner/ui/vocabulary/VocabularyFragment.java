package com.vocabulary.scanner.ui.vocabulary;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u000e\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\f0\u0015H\u0002J\u0010\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u0011H\u0002J$\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\b\u0010\"\u001a\u00020\u0011H\u0016J\u001a\u0010#\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\u001b2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u0010\u0010%\u001a\u00020\u00112\u0006\u0010&\u001a\u00020\'H\u0002J\b\u0010(\u001a\u00020\u0011H\u0002J\b\u0010)\u001a\u00020\u0011H\u0002J\b\u0010*\u001a\u00020\u0011H\u0002J\b\u0010+\u001a\u00020\u0011H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/vocabulary/scanner/ui/vocabulary/VocabularyFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/vocabulary/scanner/databinding/FragmentVocabularyBinding;", "binding", "getBinding", "()Lcom/vocabulary/scanner/databinding/FragmentVocabularyBinding;", "currentFilterType", "Lcom/vocabulary/scanner/data/VocabularyType;", "filteredList", "", "Lcom/vocabulary/scanner/ui/vocabulary/VocabularyItem;", "vocabularyAdapter", "Lcom/vocabulary/scanner/ui/vocabulary/VocabularyListAdapter;", "vocabularyList", "filterVocabulary", "", "query", "", "getHomePageVocabularyData", "", "getWordFromDefinition", "wordDef", "Lcom/vocabulary/scanner/data/WordDefinition;", "loadVocabularyData", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "openWordDetail", "position", "", "setupFilter", "setupRecyclerView", "setupSearch", "showFilterDialog", "app_debug"})
public final class VocabularyFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.vocabulary.scanner.databinding.FragmentVocabularyBinding _binding;
    private com.vocabulary.scanner.ui.vocabulary.VocabularyListAdapter vocabularyAdapter;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.vocabulary.scanner.ui.vocabulary.VocabularyItem> vocabularyList;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.vocabulary.scanner.ui.vocabulary.VocabularyItem> filteredList;
    @org.jetbrains.annotations.NotNull()
    private com.vocabulary.scanner.data.VocabularyType currentFilterType = com.vocabulary.scanner.data.VocabularyType.ALL;
    
    public VocabularyFragment() {
        super();
    }
    
    private final com.vocabulary.scanner.databinding.FragmentVocabularyBinding getBinding() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void loadVocabularyData() {
    }
    
    private final void setupSearch() {
    }
    
    private final void setupFilter() {
    }
    
    private final void showFilterDialog() {
    }
    
    private final void filterVocabulary(java.lang.String query) {
    }
    
    private final void openWordDetail(int position) {
    }
    
    private final java.util.List<com.vocabulary.scanner.ui.vocabulary.VocabularyItem> getHomePageVocabularyData() {
        return null;
    }
    
    private final java.lang.String getWordFromDefinition(com.vocabulary.scanner.data.WordDefinition wordDef) {
        return null;
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}