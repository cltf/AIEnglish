package com.cltf.aienglish.domain

import com.cltf.aienglish.word.WordVariation
import java.util.regex.Pattern

object ScanTextAnalyzer {

    private val wordPattern = Pattern.compile("\\b[a-zA-Z]+\\b")

    fun unknownWords(text: String, vocabularyKeys: Set<String>): List<String> {
        val matcher = wordPattern.matcher(text)
        val seen = HashSet<String>()
        val out = ArrayList<String>()
        while (matcher.find()) {
            val raw = matcher.group()
            val lower = raw.lowercase()
            if (lower.length <= 2) continue
            if (WordVariation.findWordInVocabulary(lower, vocabularyKeys) != null) continue
            if (seen.add(lower)) out.add(lower)
        }
        return out
    }

    fun accuracyPercent(text: String, unknownCount: Int): Int {
        val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val total = words.size
        if (total <= 0) return 100
        val known = total - unknownCount
        return kotlin.math.max(85, known * 100 / total)
    }
}
