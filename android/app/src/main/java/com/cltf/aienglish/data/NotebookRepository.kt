package com.cltf.aienglish.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * 生词本：单词（小写）为 key，唯一；记录加入次数。
 * 存储为 JSON 对象 `{"word": count}`，与 Web localStorage、iOS UserDefaults 语义一致。
 */
class NotebookRepository(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadCounts(): Map<String, Int> {
        val raw = prefs.getString(KEY_WORDS, null) ?: return emptyMap()
        return parseRaw(raw)
    }

    /** 按单词字母序，便于列表展示 */
    fun loadEntriesSorted(): List<Pair<String, Int>> =
        loadCounts().toList().sortedBy { it.first }

    fun add(word: String) {
        val w = word.lowercase().trim()
        if (w.isEmpty()) return
        val cur = loadCounts().toMutableMap()
        cur[w] = cur.getOrDefault(w, 0) + 1
        saveCounts(cur)
    }

    fun remove(word: String) {
        val w = word.lowercase()
        val cur = loadCounts().toMutableMap()
        cur.remove(w)
        saveCounts(cur)
    }

    fun clear() {
        prefs.edit().putString(KEY_WORDS, "{}").apply()
    }

    fun contains(word: String): Boolean =
        loadCounts().containsKey(word.lowercase())

    fun countFor(word: String): Int =
        loadCounts()[word.lowercase()] ?: 0

    private fun saveCounts(map: Map<String, Int>) {
        val o = JSONObject()
        map.toSortedMap().forEach { (k, v) -> if (v > 0) o.put(k, v) }
        prefs.edit().putString(KEY_WORDS, o.toString()).apply()
    }

    private fun parseRaw(raw: String): Map<String, Int> =
        runCatching {
            when {
                raw.trimStart().startsWith("[") -> {
                    val arr = JSONArray(raw)
                    buildMap {
                        for (i in 0 until arr.length()) {
                            val w = arr.getString(i).lowercase()
                            put(w, 1)
                        }
                    }
                }
                raw.trimStart().startsWith("{") -> {
                    val o = JSONObject(raw)
                    buildMap {
                        val keys = o.keys()
                        while (keys.hasNext()) {
                            val k = keys.next() as String
                            val v = o.optInt(k, 0)
                            if (v > 0) put(k.lowercase(), v)
                        }
                    }
                }
                else -> emptyMap()
            }
        }.getOrElse { emptyMap() }

    companion object {
        private const val PREFS_NAME = "aienglish_notebook_prefs"
        private const val KEY_WORDS = "aienglish_notebook"
    }
}
