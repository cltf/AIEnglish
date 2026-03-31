package com.cltf.aienglish

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cltf.aienglish.data.NotebookRepository
import com.cltf.aienglish.data.VocabularyRepository
import com.cltf.aienglish.data.WordRecord
import com.cltf.aienglish.domain.ReadingAnalysis
import com.cltf.aienglish.domain.ReadingAnalysisEngine
import com.cltf.aienglish.domain.ScanTextAnalyzer
import com.cltf.aienglish.network.AiRepository
import com.cltf.aienglish.network.DictionaryRepository
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val vocabularyRepository = VocabularyRepository(application)
    val notebookRepository = NotebookRepository(application)
    val aiRepository = AiRepository()
    val dictionaryRepository = DictionaryRepository()

    var vocabLoaded by mutableStateOf(false)
        private set

    var vocabSearch by mutableStateOf("")
    var vocabFilter by mutableStateOf("ALL")
    var vocabPage by mutableIntStateOf(1)
    private val pageSize = 20

    var notebookWords by mutableStateOf<List<String>>(emptyList())
        private set

    var fontScaleKey by mutableStateOf("standard")

    var scanBitmap by mutableStateOf<Bitmap?>(null)
    var ocrText by mutableStateOf("")
    var scanError by mutableStateOf<String?>(null)
    var ocrLoading by mutableStateOf(false)
    var aiLoading by mutableStateOf(false)
    var readingAnalysis by mutableStateOf<ReadingAnalysis?>(null)

    init {
        viewModelScope.launch {
            vocabularyRepository.load()
            vocabLoaded = true
        }
        notebookWords = notebookRepository.loadWords()
        val prefs = getApplication<Application>().getSharedPreferences("aienglish_prefs", 0)
        fontScaleKey = prefs.getString("aienglish_font", "standard") ?: "standard"
    }

    fun refreshNotebook() {
        notebookWords = notebookRepository.loadWords()
    }

    fun addNotebook(word: String) {
        notebookRepository.add(word)
        refreshNotebook()
    }

    fun removeNotebook(word: String) {
        notebookRepository.remove(word)
        refreshNotebook()
    }

    fun clearNotebook() {
        notebookRepository.clear()
        refreshNotebook()
    }

    fun setFontScale(key: String) {
        fontScaleKey = key
        getApplication<Application>().getSharedPreferences("aienglish_prefs", 0)
            .edit().putString("aienglish_font", key).apply()
    }

    val filteredVocabulary: List<WordRecord>
        get() = vocabularyRepository.listFiltered(vocabFilter, vocabSearch)

    val vocabTotalPages: Int
        get() = kotlin.math.max(1, (filteredVocabulary.size + pageSize - 1) / pageSize)

    val vocabPageItems: List<WordRecord>
        get() {
            val total = vocabTotalPages
            val p = vocabPage.coerceIn(1, total)
            val start = (p - 1) * pageSize
            return filteredVocabulary.drop(start).take(pageSize)
        }

    fun runOcr(bitmap: Bitmap) {
        viewModelScope.launch {
            ocrLoading = true
            scanError = null
            readingAnalysis = null
            try {
                val text = aiRepository.transcribeImage(bitmap)
                ocrText = text
                readingAnalysis = ReadingAnalysisEngine.analyzeHeuristic(text)
            } catch (e: Exception) {
                scanError = e.message ?: e.toString()
                ocrText = ""
            } finally {
                ocrLoading = false
            }
        }
    }

    fun runAiAnalysis() {
        val t = ocrText.trim()
        if (t.isEmpty()) return
        viewModelScope.launch {
            aiLoading = true
            scanError = null
            try {
                val raw = aiRepository.analyzeReading(t)
                val parsed = ReadingAnalysisEngine.parseAIResponse(raw)
                if (parsed != null) {
                    readingAnalysis = parsed
                } else {
                    scanError = "AI 返回无法解析为 JSON，已保留启发式结果。"
                    readingAnalysis = ReadingAnalysisEngine.analyzeHeuristic(t)
                }
            } catch (e: Exception) {
                scanError = e.message ?: e.toString()
            } finally {
                aiLoading = false
            }
        }
    }

    fun clearScan() {
        scanBitmap = null
        ocrText = ""
        scanError = null
        readingAnalysis = null
    }

    /** 保留图片，仅清空识别结果 */
    fun resetOcrResults() {
        ocrText = ""
        scanError = null
        readingAnalysis = null
    }

    fun unknownWordsForOcr(): List<String> {
        val t = ocrText.trim()
        if (t.isEmpty()) return emptyList()
        return ScanTextAnalyzer.unknownWords(t, vocabularyRepository.vocabularyKeys())
    }

    fun accuracyPercent(): Int {
        val t = ocrText.trim()
        if (t.isEmpty()) return 100
        val u = unknownWordsForOcr()
        return ScanTextAnalyzer.accuracyPercent(t, u.size)
    }
}
