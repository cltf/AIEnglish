package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class VocabularyRepository(context: Context) {

    private val appContext = context.applicationContext
    private val gson = Gson()

    @Volatile
    private var cached: List<WordRecord> = emptyList()

    @Volatile
    private var keySet: Set<String> = emptySet()

    suspend fun load(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            appContext.assets.open("vocabulary.json").use { input ->
                val file = gson.fromJson(InputStreamReader(input, Charsets.UTF_8), VocabularyFile::class.java)
                cached = file.words.sortedBy { it.word.lowercase() }
                keySet = cached.map { it.word.lowercase() }.toSet()
            }
        }
    }

    fun words(): List<WordRecord> = cached

    fun vocabularyKeys(): Set<String> = keySet

    fun recordFor(word: String): WordRecord? =
        cached.firstOrNull { it.word.equals(word, ignoreCase = true) }

    fun listFiltered(type: String, search: String): List<WordRecord> {
        val q = search.trim().lowercase()
        return cached.filter { rec ->
            if (type != "ALL" && rec.type != type) return@filter false
            if (q.isNotEmpty() && !rec.word.lowercase().contains(q)) return@filter false
            true
        }
    }

    val totalCount: Int get() = cached.size
}
