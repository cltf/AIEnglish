package com.cltf.aienglish.data

import com.google.gson.annotations.SerializedName

data class VocabularyFile(
    val version: Int? = null,
    val words: List<WordRecord> = emptyList()
)

data class WordRecord(
    val word: String = "",
    val phonetic: String = "",
    val type: String = "",
    val definitions: List<DefinitionItem> = emptyList(),
    val example: String? = null,
    @SerializedName("exampleZh") val exampleZh: String? = null
) {
    val id: String get() = word
}

data class DefinitionItem(
    val partOfSpeech: String,
    val meaning: String
)
