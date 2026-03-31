package com.cltf.aienglish.data

import android.content.Context
import org.json.JSONArray

/**
 * 生词本（与 iOS UserDefaults key 逻辑一致：JSON 数组字符串）
 */
class NotebookRepository(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadWords(): List<String> {
        val raw = prefs.getString(KEY_WORDS, "[]") ?: "[]"
        return parseArray(raw)
    }

    fun add(word: String) {
        val w = word.lowercase().trim()
        if (w.isEmpty()) return
        val cur = loadWords().toMutableSet()
        if (!cur.add(w)) return
        save(cur.sorted())
    }

    fun remove(word: String) {
        val w = word.lowercase()
        save(loadWords().filter { it != w })
    }

    fun clear() {
        prefs.edit().putString(KEY_WORDS, "[]").apply()
    }

    fun contains(word: String): Boolean =
        loadWords().any { it.equals(word, ignoreCase = true) }

    private fun save(words: List<String>) {
        prefs.edit().putString(KEY_WORDS, JSONArray(words).toString()).apply()
    }

    private fun parseArray(raw: String): List<String> =
        runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) add(arr.getString(i).lowercase())
            }.distinct().sorted()
        }.getOrElse { emptyList() }

    companion object {
        private const val PREFS_NAME = "aienglish_notebook_prefs"
        private const val KEY_WORDS = "aienglish_notebook"
    }
}
