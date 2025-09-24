package com.vocabulary.scanner.utils

/**
 * 文本解析工具类
 * 提供智能的句子分割和文本处理功能
 */
object TextParser {
    
    /**
     * 将文本分割成句子
     * 支持多种分割规则，处理复杂的文本结构
     */
    fun splitIntoSentences(text: String): List<String> {
        if (text.trim().isEmpty()) {
            return emptyList()
        }
        
        val cleanedText = preprocessText(text)
        
        // 首先按段落分割（双换行符）
        val paragraphs = cleanedText.split(Regex("\\n\\s*\\n"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        val sentences = mutableListOf<String>()
        
        for (paragraph in paragraphs) {
            // 对每个段落按句子分割
            val paragraphSentences = splitParagraphIntoSentences(paragraph)
            sentences.addAll(paragraphSentences)
        }
        
        return sentences
    }
    
    /**
     * 将段落分割成句子
     */
    private fun splitParagraphIntoSentences(paragraph: String): List<String> {
        if (paragraph.trim().isEmpty()) {
            return emptyList()
        }
        
        // 按句号、问号、感叹号分割，但排除<DOT>标记
        val sentences = paragraph.split(Regex("[.!?]+(?![^<]*<DOT>)"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        // 如果只有一个句子，检查是否包含分号
        if (sentences.size == 1) {
            val semicolonSentences = sentences[0].split(Regex("[;]+"))
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            
            if (semicolonSentences.size > 1) {
                return semicolonSentences.map { restoreDots(it) }
            }
        }
        
        return sentences.map { restoreDots(it) }
    }
    
    /**
     * 恢复句号标记
     */
    private fun restoreDots(text: String): String {
        return text.replace("<DOT>", ".")
    }
    
    /**
     * 预处理文本
     */
    private fun preprocessText(text: String): String {
        return text
            // 移除多余的空白字符
            .replace(Regex("\\s+"), " ")
            // 处理常见的缩写（在句号前添加特殊标记，避免被分割）
            .replace(Regex("\\b(Mr|Mrs|Ms|Dr|Prof)\\.\\s+"), "$1<DOT> ")
            // 处理数字后的句号（如 "1. 2. 3."）
            .replace(Regex("(\\d+)\\.\\s+"), "$1<DOT> ")
            // 处理引号内的内容
            .replace(Regex("\"([^\"]+)\""), "\"$1\"")
            // 处理括号内的内容
            .replace(Regex("\\(([^)]+)\\)"), "($1)")
            // 移除行首行尾空白
            .trim()
    }
    
    /**
     * 将文本分割成段落
     */
    fun splitIntoParagraphs(text: String): List<String> {
        if (text.trim().isEmpty()) {
            return emptyList()
        }
        
        return text.split(Regex("\\n\\s*\\n"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
    
    /**
     * 将文本分割成单词
     */
    fun splitIntoWords(text: String): List<String> {
        if (text.trim().isEmpty()) {
            return emptyList()
        }
        
        return text.split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
    
    /**
     * 检测文本语言
     */
    fun detectLanguage(text: String): String {
        val chinesePattern = Regex("[\\u4e00-\\u9fff]")
        val englishPattern = Regex("[a-zA-Z]")
        
        val chineseCount = chinesePattern.findAll(text).count()
        val englishCount = englishPattern.findAll(text).count()
        
        return when {
            chineseCount > englishCount -> "zh"
            englishCount > chineseCount -> "en"
            else -> "unknown"
        }
    }
    
    /**
     * 清理文本
     */
    fun cleanText(text: String): String {
        return text
            // 移除多余的空白字符
            .replace(Regex("\\s+"), " ")
            // 移除特殊字符
            .replace(Regex("[^\\w\\s\\u4e00-\\u9fff.,!?;:()\"'-]"), "")
            // 移除行首行尾空白
            .trim()
    }
    
    /**
     * 提取文本中的关键词
     */
    fun extractKeywords(text: String, maxKeywords: Int = 10): List<String> {
        val words = splitIntoWords(text.lowercase())
        
        // 过滤停用词
        val stopWords = setOf(
            "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
            "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "do", "does", "did",
            "will", "would", "could", "should", "may", "might", "must", "can", "this", "that", "these", "those",
            "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them"
        )
        
        val filteredWords = words.filter { word ->
            word.length > 2 && !stopWords.contains(word)
        }
        
        // 统计词频
        val wordCount = mutableMapOf<String, Int>()
        filteredWords.forEach { word ->
            wordCount[word] = wordCount.getOrDefault(word, 0) + 1
        }
        
        // 按词频排序并返回前N个关键词
        return wordCount.entries
            .sortedByDescending { it.value }
            .take(maxKeywords)
            .map { it.key }
    }
    
    /**
     * 计算文本相似度
     */
    fun calculateSimilarity(text1: String, text2: String): Double {
        val words1 = splitIntoWords(text1.lowercase()).toSet()
        val words2 = splitIntoWords(text2.lowercase()).toSet()
        
        val intersection = words1.intersect(words2)
        val union = words1.union(words2)
        
        return if (union.isEmpty()) 0.0 else intersection.size.toDouble() / union.size
    }
    
    /**
     * 格式化文本用于显示
     */
    fun formatTextForDisplay(text: String): String {
        return text
            // 确保句子首字母大写
            .split(Regex("[.!?]+"))
            .map { sentence ->
                sentence.trim().let { s ->
                    if (s.isNotEmpty()) {
                        s[0].uppercaseChar() + s.substring(1)
                    } else s
                }
            }
            .joinToString(". ")
            // 确保以句号结尾
            .let { if (it.isNotEmpty() && !it.endsWith(".") && !it.endsWith("!") && !it.endsWith("?")) "$it." else it }
    }
}
