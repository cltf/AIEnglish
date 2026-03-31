package com.cltf.aienglish.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class DictionaryRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetchExample(word: String): String? = withContext(Dispatchers.IO) {
        val w = word.lowercase().trim()
        if (w.isEmpty()) return@withContext null
        val slugs = if (w.contains(' ')) listOf(w, w.split(' ').first()) else listOf(w)
        for (slug in slugs) {
            val enc = URLEncoder.encode(slug, "UTF-8").replace("+", "%20")
            val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$enc"
            val req = Request.Builder().url(url).build()
            runCatching {
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use null
                    val body = resp.body?.string() ?: return@use null
                    val arr = org.json.JSONArray(body)
                    extractExample(arr, slug)
                }
            }.getOrNull()?.let { return@withContext it }
        }
        null
    }

    private fun extractExample(entries: org.json.JSONArray, word: String): String? {
        val candidates = mutableListOf<String>()
        for (i in 0 until entries.length()) {
            val entry = entries.optJSONObject(i) ?: continue
            val meanings = entry.optJSONArray("meanings") ?: continue
            for (j in 0 until meanings.length()) {
                val m = meanings.optJSONObject(j) ?: continue
                val defs = m.optJSONArray("definitions") ?: continue
                for (k in 0 until defs.length()) {
                    val d = defs.optJSONObject(k) ?: continue
                    val ex = d.optString("example").trim()
                    if (ex.isNotEmpty()) candidates.add(ex)
                }
            }
        }
        if (candidates.isEmpty()) return null
        val escaped = Pattern.quote(word)
        val boundary = Pattern.compile("\\b$escaped\\b", Pattern.CASE_INSENSITIVE)
        for (ex in candidates) {
            if (boundary.matcher(ex).find()) {
                return if (ex.endsWith(".") || ex.endsWith("!") || ex.endsWith("?")) ex else "$ex."
            }
        }
        val first = candidates.first()
        return if (first.endsWith(".") || first.endsWith("!") || first.endsWith("?")) first else "$first."
    }

    suspend fun translateToZh(english: String): String? = withContext(Dispatchers.IO) {
        val t = english.trim()
        if (t.isEmpty()) return@withContext null
        val q = URLEncoder.encode(t, "UTF-8")
        val url = "https://api.mymemory.translated.net/get?q=$q&langpair=en|zh-CN"
        val req = Request.Builder().url(url).build()
        runCatching {
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@use null
                val obj = JSONObject(resp.body?.string() ?: return@use null)
                val rd = obj.optJSONObject("responseData") ?: return@use null
                rd.optString("translatedText").trim().takeIf { it.isNotEmpty() }
            }
        }.getOrNull()
    }
}
