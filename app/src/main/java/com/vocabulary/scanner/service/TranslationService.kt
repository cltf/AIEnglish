package com.vocabulary.scanner.service

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 翻译服务提供商枚举
 */
enum class TranslationProvider {
    BAIDU,      // 百度翻译
    GOOGLE,     // Google翻译
    YOUDAO,     // 有道翻译
    LOCAL       // 本地词典
}

/**
 * 翻译服务类
 * 支持多种翻译API：百度翻译、Google翻译、有道翻译等
 */
class TranslationService(private val context: Context) {
    
    private val cacheManager = TranslationCacheManager(context)
    
    companion object {
        private const val TAG = "TranslationService"
        
        // 百度翻译API配置
        private const val BAIDU_APP_ID = "your_baidu_app_id"
        private const val BAIDU_SECRET_KEY = "your_baidu_secret_key"
        private const val BAIDU_API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate"
        
        // Google翻译API配置
        private const val GOOGLE_API_KEY = "your_google_api_key"
        private const val GOOGLE_API_URL = "https://translation.googleapis.com/language/translate/v2"
        
        // 有道翻译API配置
        private const val YOUDAO_APP_KEY = "your_youdao_app_key"
        private const val YOUDAO_APP_SECRET = "your_youdao_app_secret"
        private const val YOUDAO_API_URL = "https://openapi.youdao.com/api"
    }
    
    /**
     * 翻译文本
     * @param text 要翻译的文本
     * @param from 源语言
     * @param to 目标语言
     * @param provider 翻译服务提供商
     * @return 翻译结果
     */
    suspend fun translate(
        text: String,
        from: String = "en",
        to: String = "zh",
        provider: TranslationProvider = TranslationProvider.BAIDU
    ): TranslationResult = withContext(Dispatchers.IO) {
        try {
            // 先检查缓存
            val cachedResult = cacheManager.getCachedTranslation(text, from, to, provider)
            if (cachedResult != null) {
                Log.d(TAG, "Translation found in cache")
                return@withContext cachedResult
            }
            
            // 执行翻译
            val result = when (provider) {
                TranslationProvider.BAIDU -> translateWithBaidu(text, from, to)
                TranslationProvider.GOOGLE -> translateWithGoogle(text, from, to)
                TranslationProvider.YOUDAO -> translateWithYoudao(text, from, to)
                TranslationProvider.LOCAL -> translateWithLocal(text, from, to)
            }
            
            // 缓存翻译结果
            if (result.success) {
                cacheManager.cacheTranslation(text, from, to, provider, result)
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Translation failed", e)
            TranslationResult(
                success = false,
                originalText = text,
                translatedText = "翻译失败：${e.message}",
                error = e.message
            )
        }
    }
    
    /**
     * 使用百度翻译API
     */
    private suspend fun translateWithBaidu(text: String, from: String, to: String): TranslationResult {
        val salt = System.currentTimeMillis().toString()
        val sign = generateBaiduSign(text, salt)
        
        val params = mapOf(
            "q" to text,
            "from" to from,
            "to" to to,
            "appid" to BAIDU_APP_ID,
            "salt" to salt,
            "sign" to sign
        )
        
        val response = makeHttpRequest(BAIDU_API_URL, params)
        return parseBaiduResponse(response, text)
    }
    
    /**
     * 使用Google翻译API
     */
    private suspend fun translateWithGoogle(text: String, from: String, to: String): TranslationResult {
        val params = mapOf(
            "key" to GOOGLE_API_KEY,
            "q" to text,
            "source" to from,
            "target" to to,
            "format" to "text"
        )
        
        val response = makeHttpRequest(GOOGLE_API_URL, params)
        return parseGoogleResponse(response, text)
    }
    
    /**
     * 使用有道翻译API
     */
    private suspend fun translateWithYoudao(text: String, from: String, to: String): TranslationResult {
        val salt = System.currentTimeMillis().toString()
        val curtime = (System.currentTimeMillis() / 1000).toString()
        val sign = generateYoudaoSign(text, salt, curtime)
        
        val params = mapOf(
            "q" to text,
            "from" to from,
            "to" to to,
            "appKey" to YOUDAO_APP_KEY,
            "salt" to salt,
            "sign" to sign,
            "signType" to "v3",
            "curtime" to curtime
        )
        
        val response = makeHttpRequest(YOUDAO_API_URL, params)
        return parseYoudaoResponse(response, text)
    }
    
    /**
     * 使用本地词典翻译
     */
    private suspend fun translateWithLocal(text: String, from: String, to: String): TranslationResult {
        // 使用本地词典进行翻译
        val localTranslations = getLocalTranslations()
        val translatedText = translateWithLocalDictionary(text, localTranslations)
        
        return TranslationResult(
            success = true,
            originalText = text,
            translatedText = translatedText,
            provider = "本地词典"
        )
    }
    
    /**
     * 生成百度翻译签名
     */
    private fun generateBaiduSign(text: String, salt: String): String {
        val signString = BAIDU_APP_ID + text + salt + BAIDU_SECRET_KEY
        return md5(signString)
    }
    
    /**
     * 生成有道翻译签名
     */
    private fun generateYoudaoSign(text: String, salt: String, curtime: String): String {
        val input = if (text.length <= 20) text else text.substring(0, 10) + text.length + text.substring(text.length - 10)
        val signString = YOUDAO_APP_KEY + input + salt + curtime + YOUDAO_APP_SECRET
        return sha256(signString)
    }
    
    /**
     * 发送HTTP请求
     */
    private suspend fun makeHttpRequest(url: String, params: Map<String, String>): String {
        val urlWithParams = buildUrlWithParams(url, params)
        val connection = URL(urlWithParams).openConnection() as HttpURLConnection
        
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                response.toString()
            } else {
                throw Exception("HTTP error: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }
    
    /**
     * 构建带参数的URL
     */
    private fun buildUrlWithParams(baseUrl: String, params: Map<String, String>): String {
        val urlBuilder = StringBuilder(baseUrl)
        urlBuilder.append("?")
        
        params.forEach { (key, value) ->
            urlBuilder.append(URLEncoder.encode(key, "UTF-8"))
            urlBuilder.append("=")
            urlBuilder.append(URLEncoder.encode(value, "UTF-8"))
            urlBuilder.append("&")
        }
        
        // 移除最后的&
        if (urlBuilder.endsWith("&")) {
            urlBuilder.setLength(urlBuilder.length - 1)
        }
        
        return urlBuilder.toString()
    }
    
    /**
     * 解析百度翻译响应
     */
    private fun parseBaiduResponse(response: String, originalText: String): TranslationResult {
        return try {
            val json = JSONObject(response)
            if (json.has("trans_result")) {
                val transResult = json.getJSONArray("trans_result")
                val firstResult = transResult.getJSONObject(0)
                val translatedText = firstResult.getString("dst")
                
                TranslationResult(
                    success = true,
                    originalText = originalText,
                    translatedText = translatedText,
                    provider = "百度翻译"
                )
            } else {
                val errorCode = json.optString("error_code", "unknown")
                val errorMsg = json.optString("error_msg", "翻译失败")
                TranslationResult(
                    success = false,
                    originalText = originalText,
                    translatedText = "翻译失败：$errorMsg",
                    error = "Error code: $errorCode"
                )
            }
        } catch (e: Exception) {
            TranslationResult(
                success = false,
                originalText = originalText,
                translatedText = "解析响应失败",
                error = e.message
            )
        }
    }
    
    /**
     * 解析Google翻译响应
     */
    private fun parseGoogleResponse(response: String, originalText: String): TranslationResult {
        return try {
            val json = JSONObject(response)
            val data = json.getJSONObject("data")
            val translations = data.getJSONArray("translations")
            val firstTranslation = translations.getJSONObject(0)
            val translatedText = firstTranslation.getString("translatedText")
            
            TranslationResult(
                success = true,
                originalText = originalText,
                translatedText = translatedText,
                provider = "Google翻译"
            )
        } catch (e: Exception) {
            TranslationResult(
                success = false,
                originalText = originalText,
                translatedText = "解析响应失败",
                error = e.message
            )
        }
    }
    
    /**
     * 解析有道翻译响应
     */
    private fun parseYoudaoResponse(response: String, originalText: String): TranslationResult {
        return try {
            val json = JSONObject(response)
            val errorCode = json.optString("errorCode", "0")
            
            if (errorCode == "0") {
                val translation = json.optJSONArray("translation")
                val translatedText = if (translation != null && translation.length() > 0) {
                    translation.getString(0)
                } else {
                    "翻译结果为空"
                }
                
                TranslationResult(
                    success = true,
                    originalText = originalText,
                    translatedText = translatedText,
                    provider = "有道翻译"
                )
            } else {
                TranslationResult(
                    success = false,
                    originalText = originalText,
                    translatedText = "翻译失败",
                    error = "Error code: $errorCode"
                )
            }
        } catch (e: Exception) {
            TranslationResult(
                success = false,
                originalText = originalText,
                translatedText = "解析响应失败",
                error = e.message
            )
        }
    }
    
    /**
     * 获取本地翻译词典
     */
    private fun getLocalTranslations(): Map<String, String> {
        return mapOf(
            // 基础词汇
            "hello" to "你好",
            "world" to "世界",
            "good" to "好的",
            "morning" to "早上",
            "afternoon" to "下午",
            "evening" to "晚上",
            "night" to "夜晚",
            "thank" to "谢谢",
            "please" to "请",
            "sorry" to "对不起",
            "yes" to "是的",
            "no" to "不",
            
            // 疑问词
            "how" to "如何",
            "what" to "什么",
            "where" to "哪里",
            "when" to "什么时候",
            "why" to "为什么",
            "who" to "谁",
            "which" to "哪一个",
            
            // 学校相关
            "school" to "学校",
            "student" to "学生",
            "teacher" to "老师",
            "book" to "书",
            "pen" to "笔",
            "pencil" to "铅笔",
            "desk" to "桌子",
            "chair" to "椅子",
            "classroom" to "教室",
            "homework" to "作业",
            "exam" to "考试",
            "test" to "测试",
            
            // 家庭相关
            "family" to "家庭",
            "father" to "父亲",
            "mother" to "母亲",
            "brother" to "兄弟",
            "sister" to "姐妹",
            "parent" to "父母",
            "child" to "孩子",
            "baby" to "婴儿",
            
            // 时间相关
            "time" to "时间",
            "hour" to "小时",
            "minute" to "分钟",
            "second" to "秒",
            "day" to "天",
            "week" to "周",
            "month" to "月",
            "year" to "年",
            "today" to "今天",
            "tomorrow" to "明天",
            "yesterday" to "昨天",
            
            // 颜色
            "red" to "红色",
            "blue" to "蓝色",
            "green" to "绿色",
            "yellow" to "黄色",
            "black" to "黑色",
            "white" to "白色",
            "pink" to "粉色",
            "purple" to "紫色",
            "orange" to "橙色",
            "brown" to "棕色",
            
            // 数字
            "one" to "一",
            "two" to "二",
            "three" to "三",
            "four" to "四",
            "five" to "五",
            "six" to "六",
            "seven" to "七",
            "eight" to "八",
            "nine" to "九",
            "ten" to "十",
            
            // 动词
            "go" to "去",
            "come" to "来",
            "see" to "看",
            "look" to "看",
            "listen" to "听",
            "speak" to "说",
            "talk" to "谈话",
            "read" to "读",
            "write" to "写",
            "study" to "学习",
            "learn" to "学习",
            "teach" to "教",
            "work" to "工作",
            "play" to "玩",
            "eat" to "吃",
            "drink" to "喝",
            "sleep" to "睡觉",
            "walk" to "走",
            "run" to "跑",
            "jump" to "跳",
            "swim" to "游泳",
            "fly" to "飞",
            "drive" to "驾驶",
            "buy" to "买",
            "sell" to "卖",
            "give" to "给",
            "take" to "拿",
            "make" to "制作",
            "do" to "做",
            "get" to "得到",
            "have" to "有",
            "be" to "是",
            
            // 情态动词
            "will" to "将",
            "can" to "能",
            "may" to "可能",
            "must" to "必须",
            "should" to "应该",
            "would" to "会",
            "could" to "能够",
            "might" to "可能",
            "shall" to "将",
            
            // 形容词
            "good" to "好的",
            "bad" to "坏的",
            "big" to "大的",
            "small" to "小的",
            "long" to "长的",
            "short" to "短的",
            "tall" to "高的",
            "short" to "矮的",
            "old" to "老的",
            "new" to "新的",
            "young" to "年轻的",
            "beautiful" to "美丽的",
            "ugly" to "丑陋的",
            "happy" to "快乐的",
            "sad" to "悲伤的",
            "angry" to "生气的",
            "tired" to "累的",
            "hungry" to "饿的",
            "thirsty" to "渴的",
            "hot" to "热的",
            "cold" to "冷的",
            "warm" to "温暖的",
            "cool" to "凉爽的",
            "fast" to "快的",
            "slow" to "慢的",
            "easy" to "容易的",
            "difficult" to "困难的",
            "important" to "重要的",
            "interesting" to "有趣的",
            "boring" to "无聊的",
            "expensive" to "昂贵的",
            "cheap" to "便宜的",
            "free" to "免费的",
            "busy" to "忙碌的",
            "free" to "空闲的",
            "clean" to "干净的",
            "dirty" to "脏的",
            "full" to "满的",
            "empty" to "空的",
            "open" to "开的",
            "closed" to "关的",
            "right" to "对的",
            "wrong" to "错的",
            "true" to "真的",
            "false" to "假的"
        )
    }
    
    /**
     * 使用本地词典翻译
     */
    private fun translateWithLocalDictionary(text: String, dictionary: Map<String, String>): String {
        val words = text.lowercase().split("\\s+".toRegex())
        val translatedWords = mutableListOf<String>()
        
        for (word in words) {
            val cleanWord = word.replace(Regex("[^a-zA-Z]"), "")
            val translation = dictionary[cleanWord] ?: word
            translatedWords.add(translation)
        }
        
        return translatedWords.joinToString(" ")
    }
    
    /**
     * MD5加密
     */
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * SHA256加密
     */
    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 清除翻译缓存
     */
    fun clearCache() {
        cacheManager.clearAllCache()
    }
    
    /**
     * 清除过期缓存
     */
    fun clearExpiredCache() {
        cacheManager.clearExpiredCache()
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getCacheStats(): TranslationCacheManager.CacheStats {
        return cacheManager.getCacheStats()
    }
}

/**
 * 翻译结果数据类
 */
data class TranslationResult(
    val success: Boolean,
    val originalText: String,
    val translatedText: String,
    val provider: String = "",
    val error: String? = null
)
