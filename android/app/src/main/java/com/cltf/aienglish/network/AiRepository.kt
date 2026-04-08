package com.cltf.aienglish.network

import android.graphics.Bitmap
import android.util.Base64
import com.cltf.aienglish.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class AiRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val url: String
        get() = "http://${BuildConfig.AI_PROXY_HOST}:8787/openai-compatible/v1/chat/completions"
    private val ttsUrl: String
        get() = "http://${BuildConfig.AI_PROXY_HOST}:8787/openai-compatible/v1/essay-tts"

    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    companion object {
        const val DEFAULT_OCR_MODEL = "gemini-2.5-flash-image"
        const val DEFAULT_READ_MODEL = "gemini-3-pro"
    }

    suspend fun transcribeImage(bitmap: Bitmap, ocrModel: String = DEFAULT_OCR_MODEL): String = withContext(Dispatchers.IO) {
        val jpeg = bitmapToJpeg(bitmap)
        val b64 = Base64.encodeToString(jpeg, Base64.NO_WRAP)
        val dataUrl = "data:image/jpeg;base64,$b64"
        val model = ocrModel.ifEmpty { DEFAULT_OCR_MODEL }
        val userContent = JSONArray()
            .put(JSONObject().put("type", "text").put("text", "Extract all English text from this image. If there is no English, output an empty string."))
            .put(
                JSONObject()
                    .put("type", "image_url")
                    .put("image_url", JSONObject().put("url", dataUrl))
            )
        val messages = JSONArray()
            .put(
                JSONObject().put(
                    "role", "system"
                ).put("content", "You transcribe English text from photos of textbooks and exam papers. Output only the English text from the image, preserving line breaks where natural. Do not translate or explain.")
            )
            .put(JSONObject().put("role", "user").put("content", userContent))
        val body = JSONObject()
            .put("model", model)
            .put("messages", messages)
            .put("temperature", 0.1)
            .put("max_tokens", 8192)
        postJson(body).let { contentString(it) }.trim()
    }

    suspend fun analyzeReading(text: String, readModel: String = DEFAULT_READ_MODEL): String = withContext(Dispatchers.IO) {
        val sample = if (text.length > 12000) text.take(12000) + "\n\n…（已截断）" else text
        val prompt = """
            以下英文可能来自 OCR。请用中文完成分析，并只输出一个 JSON 对象（不要使用 markdown 代码围栏），键名必须完全一致：
            {
              "paragraphGists": [{"index":1,"gistZh":"该段主旨的中文概括","keySentenceEn":"从原文摘一句最能代表该段的英文原句"}],
              "coreViewpointZh":"全文核心观点（中文）",
              "examPoints": ["中考阅读高频考点提示1","提示2"],
              "logicRelations": ["如：因果/转折/例证 与答题注意"],
              "howToSolveZh":"针对此类文章与常见设问，学生应如何审题与作答（分条，用\\n换行）"
            }
            英文文本：
            ---
            $sample
            ---
        """.trimIndent()
        val model = readModel.ifEmpty { DEFAULT_READ_MODEL }
        val messages = JSONArray()
            .put(JSONObject().put("role", "system").put("content", "你是中考英语阅读教研助手，只输出合法 JSON，不要任何解释文字。"))
            .put(JSONObject().put("role", "user").put("content", prompt))
        val body = JSONObject()
            .put("model", model)
            .put("messages", messages)
            .put("temperature", 0.35)
        postJson(body).let { contentString(it) }.trim()
    }

    suspend fun synthesizeEssayTts(text: String): ByteArray = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("model", "gemini-2.5-flash-tts")
            .put("voiceName", "Kore")
            .put("text", text)
        val req = Request.Builder()
            .url(ttsUrl)
            .post(body.toString().toRequestBody(jsonMedia))
            .build()
        client.newCall(req).execute().use { resp ->
            val bytes = resp.body?.bytes() ?: ByteArray(0)
            if (!resp.isSuccessful) {
                val t = bytes.toString(Charsets.UTF_8)
                throw IllegalStateException("HTTP ${resp.code}: ${t.take(400)}")
            }
            if (bytes.isEmpty()) throw IllegalStateException("TTS 返回空音频")
            bytes
        }
    }

    private fun bitmapToJpeg(bitmap: Bitmap): ByteArray {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, bos)
        return bos.toByteArray()
    }

    private fun postJson(body: JSONObject): JSONObject {
        val req = Request.Builder()
            .url(url)
            .post(body.toString().toRequestBody(jsonMedia))
            .build()
        client.newCall(req).execute().use { resp ->
            val bytes = resp.body?.bytes() ?: ByteArray(0)
            if (!resp.isSuccessful) {
                val t = bytes.toString(Charsets.UTF_8)
                throw IllegalStateException("HTTP ${resp.code}: ${t.take(400)}")
            }
            return JSONObject(bytes.toString(Charsets.UTF_8))
        }
    }

    private fun contentString(chat: JSONObject): String {
        val choices = chat.optJSONArray("choices") ?: return ""
        val first = choices.optJSONObject(0) ?: return ""
        val msg = first.optJSONObject("message") ?: return ""
        val c = msg.opt("content") ?: return ""
        return when (c) {
            is String -> c
            is JSONArray -> {
                buildString {
                    for (i in 0 until c.length()) {
                        val part = c.optJSONObject(i)
                        append(part?.optString("text") ?: "")
                    }
                }
            }
            else -> ""
        }
    }
}
