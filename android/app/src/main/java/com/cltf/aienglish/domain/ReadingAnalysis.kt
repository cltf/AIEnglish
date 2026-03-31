package com.cltf.aienglish.domain

data class ParagraphGist(
    val index: Int,
    val gistZh: String,
    val keySentenceEn: String
)

data class ReadingAnalysis(
    val mode: String,
    val paragraphGists: List<ParagraphGist>,
    val coreViewpointZh: String,
    val examPoints: List<String>,
    val logicRelations: List<String>,
    val howToSolveZh: String
)
