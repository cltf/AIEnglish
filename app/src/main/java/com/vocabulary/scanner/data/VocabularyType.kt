package com.vocabulary.scanner.data

/**
 * 词汇类型枚举
 * 用于区分不同类型的词汇
 */
enum class VocabularyType(val displayName: String, val description: String) {
    /**
     * 中考词汇 - 中考英语考试要求的核心词汇
     */
    MIDDLE_SCHOOL("中考词汇", "中考英语考试要求的核心词汇"),
    
    /**
     * 超纲词汇 - 用户导入的超出中考范围的词汇
     */
    ADVANCED("超纲词汇", "超出中考范围的词汇"),
    
    /**
     * 所有词汇 - 包含所有类型的词汇
     */
    ALL("所有词汇", "包含所有类型的词汇")
}



