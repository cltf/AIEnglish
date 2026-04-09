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

data class EssaysFile(
    val version: Int? = null,
    val exams: List<EssayExam> = emptyList()
)

data class EssayExam(
    val id: String = "",
    val title: String = "",
    val topics: String = "",
    val samples: List<EssaySample> = emptyList(),
    /** `english` 北京中考英语作文；`chinese` 语文作文 */
    val subject: String = "english"
)

data class EssaySample(
    val id: String = "",
    val title: String = "",
    val body: String = ""
)

data class ReadingContentFile(
    val version: Int? = null,
    val subjects: List<ReadingSubject> = emptyList()
)

data class ReadingSubject(
    val id: String = "",
    val label: String = "",
    val packs: List<ReadingPack> = emptyList()
)

data class ReadingPack(
    val id: String = "",
    val title: String = "",
    val sections: List<ReadingSection> = emptyList(),
    val footer: String? = null
)

data class ReadingSection(
    val id: String = "",
    val headline: String = "",
    val body: String = ""
)

data class ReadingHighFreqFile(
    val version: Int? = null,
    val label: String = "",
    val note: String? = null,
    val entries: List<ReadingHighFreqEntry> = emptyList()
)

data class ReadingHighFreqEntry(
    val rank: Int = 0,
    val word: String = "",
    val phonetic: String = "",
    val meaning: String = "",
    val frequency: Int = 0
)

data class Mc688File(
    val version: Int? = null,
    val label: String = "",
    val subtitle: String? = null,
    val entries: List<Mc688Entry> = emptyList()
)

data class Mc688Entry(
    val rank: Int = 0,
    val day: Int = 0,
    val word: String = "",
    val meaning: String = ""
)

data class ReadingSkillsFile(
    val version: Int? = null,
    val label: String = "",
    val intro: String? = null,
    val topics: List<ReadingSkillTopic> = emptyList()
)

data class ReadingSkillTopic(
    val id: String = "",
    val title: String = "",
    val summary: String? = null,
    val sections: List<ReadingSkillSection> = emptyList()
)

data class ReadingSkillSection(
    val subtitle: String? = null,
    val paragraph: String? = null,
    val bullets: List<String>? = null
)

data class DaofaPastExamsFile(
    val version: Int? = null,
    val label: String? = null,
    val items: List<DaofaPastExamItem> = emptyList()
)

data class DaofaPastExamItem(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    /** 相对路径：如 math2025/page01.png（Android assets / Web data 根下） */
    val images: List<String>? = null
)

/** 北京中考各科试卷结构 JSON（physics、math、chinese、daofa、english 等 *_beijing_structure.json） */
data class PhysicsStructureFile(
    val version: Int? = null,
    val title: String = "",
    val subtitle: String? = null,
    val badge: String? = null,
    val sections: List<PhysicsSection> = emptyList()
)

data class PhysicsSection(
    val title: String = "",
    val blocks: List<PhysicsBlock> = emptyList()
)

data class PhysicsBlock(
    val type: String = "",
    val text: String? = null,
    val title: String? = null,
    val headers: List<String>? = null,
    val rows: List<List<String>>? = null,
    val items: List<String>? = null,
    val pairs: List<List<String>>? = null
)
