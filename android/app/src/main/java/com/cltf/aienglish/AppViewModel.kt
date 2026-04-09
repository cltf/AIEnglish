package com.cltf.aienglish

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cltf.aienglish.data.DefinitionItem
import com.cltf.aienglish.data.EssayExam
import com.cltf.aienglish.data.EssayRepository
import com.cltf.aienglish.data.Mc688Entry
import com.cltf.aienglish.data.Mc688Repository
import com.cltf.aienglish.data.NotebookRepository
import com.cltf.aienglish.data.ReadingHighFreqEntry
import com.cltf.aienglish.data.ReadingHighFreqRepository
import com.cltf.aienglish.data.ReadingRepository
import com.cltf.aienglish.data.ReadingSubject
import com.cltf.aienglish.data.VocabularyRepository
import com.cltf.aienglish.data.WordRecord
import com.cltf.aienglish.domain.ReadingAnalysis
import com.cltf.aienglish.domain.ReadingAnalysisEngine
import com.cltf.aienglish.domain.ScanTextAnalyzer
import com.cltf.aienglish.network.AiRepository
import com.cltf.aienglish.network.DictionaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.Volatile

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val vocabularyRepository = VocabularyRepository(application)
    val essayRepository = EssayRepository(application)
    val readingRepository = ReadingRepository(application)
    val notebookRepository = NotebookRepository(application)
    val aiRepository = AiRepository()
    val dictionaryRepository = DictionaryRepository()

    var vocabLoaded by mutableStateOf(false)
        private set

    var essaysLoaded by mutableStateOf(false)
        private set

    var essayLoadError by mutableStateOf<String?>(null)
        private set

    var essayExams by mutableStateOf<List<EssayExam>>(emptyList())
        private set

    var readingLoaded by mutableStateOf(false)
        private set

    var readingLoadError by mutableStateOf<String?>(null)
        private set

    var readingSubjects by mutableStateOf<List<ReadingSubject>>(emptyList())
        private set

    var vocabSearch by mutableStateOf("")
    var vocabFilter by mutableStateOf("ALL")
    var vocabPage by mutableIntStateOf(1)
    private val pageSize = 20

    /** 生词本条目（单词小写 → 累计加入次数），按字母序 */
    var notebookEntries by mutableStateOf<List<Pair<String, Int>>>(emptyList())
        private set

    /** 阅读高频词：小写 → 条目（用于单词详情补全音标/释义） */
    @Volatile
    private var readingHighFreqByWord: Map<String, ReadingHighFreqEntry> = emptyMap()

    /** 688 词表：小写 → 条目 */
    @Volatile
    private var mc688ByWord: Map<String, Mc688Entry> = emptyMap()

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
            essayRepository.load().fold(
                onSuccess = {
                    essayExams = essayRepository.exams()
                    essayLoadError = null
                },
                onFailure = { e ->
                    essayExams = emptyList()
                    essayLoadError = e.message ?: e.toString()
                }
            )
            essaysLoaded = true
            readingRepository.load().fold(
                onSuccess = {
                    readingSubjects = readingRepository.subjects()
                    readingLoadError = null
                },
                onFailure = { e ->
                    readingSubjects = emptyList()
                    readingLoadError = e.message ?: e.toString()
                }
            )
            readingLoaded = true
            withContext(Dispatchers.IO) {
                runCatching {
                    val hf = ReadingHighFreqRepository.load(getApplication())
                    readingHighFreqByWord = hf.entries.associateBy { it.word.lowercase() }
                }
                runCatching {
                    val mc = Mc688Repository.load(getApplication())
                    mc688ByWord = mc.entries.associateBy { it.word.lowercase() }
                }
            }
        }
        notebookEntries = notebookRepository.loadEntriesSorted()
        val prefs = getApplication<Application>().getSharedPreferences("aienglish_prefs", 0)
        fontScaleKey = prefs.getString("aienglish_font", "standard") ?: "standard"
    }

    fun refreshNotebook() {
        notebookEntries = notebookRepository.loadEntriesSorted()
    }

    fun notebookCountFor(word: String): Int = notebookRepository.countFor(word)

    var notebookToast by mutableStateOf<String?>(null)
        private set

    private var notebookToastJob: Job? = null

    fun addNotebook(word: String) {
        notebookRepository.add(word)
        refreshNotebook()
        notebookToast = "加入生词本成功"
        notebookToastJob?.cancel()
        notebookToastJob = viewModelScope.launch {
            delay(2000)
            notebookToast = null
        }
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

    /**
     * 合并主词库、阅读高频、688 词表，供详情页展示音标与释义（生词本中可能仅有非词库来源的词）。
     */
    fun resolvedWordRecord(word: String): WordRecord? {
        val key = word.lowercase()
        val base = vocabularyRepository.recordFor(word)
        val hf = readingHighFreqByWord[key]
        val mc = mc688ByWord[key]
        if (base != null) {
            val phonetic = base.phonetic.trim().ifEmpty { hf?.phonetic?.trim().orEmpty() }
            val defs = when {
                base.definitions.isNotEmpty() -> base.definitions
                hf != null -> listOf(DefinitionItem("", hf.meaning))
                mc != null -> listOf(DefinitionItem("", mc.meaning))
                else -> emptyList()
            }
            return base.copy(phonetic = phonetic, definitions = defs.ifEmpty { base.definitions })
        }
        if (hf != null) {
            return WordRecord(
                word = hf.word,
                phonetic = hf.phonetic,
                type = "",
                definitions = listOf(DefinitionItem("", hf.meaning)),
                example = null,
                exampleZh = null
            )
        }
        if (mc != null) {
            return WordRecord(
                word = mc.word,
                phonetic = "",
                type = "",
                definitions = listOf(DefinitionItem("", mc.meaning)),
                example = null,
                exampleZh = null
            )
        }
        return null
    }
}
