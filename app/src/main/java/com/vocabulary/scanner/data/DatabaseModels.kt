package com.vocabulary.scanner.data

/**
 * 单词定义数据类
 */
data class WordDefinition(
    val word: String,
    val phonetic: String,
    val definitions: List<Definition>,
    val example: String
)

/**
 * 释义数据类
 */
data class Definition(
    val partOfSpeech: String,
    val meaning: String
)

