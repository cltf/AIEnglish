package com.cltf.aienglish.domain

import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

object ReadingAnalysisEngine {

    private val staticHowToSolve = """
        |1. 先读题干与选项，圈出题干关键词（人名、地名、数字、大写、段落号）。
        |2. 细节题用关键词回原文定位，同义改写多为正确选项。
        |3. 主旨/标题题重点读首段、尾段与各段首句，排除片面细节项。
        |4. 词义猜测题看该词前后句的同义复述、举例或对比线索。
        |5. 推断题只选「能从原文合理推出」的选项，忌主观臆断与过度引申。
        |6. 注意路标词：转折、因果、递进、举例，常对应考点与正确项。
    """.trimMargin()

    private val logicPatterns: List<Pair<Pattern, String>> = listOf(
        Pattern.compile("\\b(however|but|yet|instead|while)\\b", Pattern.CASE_INSENSITIVE) to "转折对比",
        Pattern.compile("\\b(because|since|as|so that)\\b", Pattern.CASE_INSENSITIVE) to "原因目的",
        Pattern.compile("\\b(therefore|thus|so|as a result|consequently)\\b", Pattern.CASE_INSENSITIVE) to "结果结论",
        Pattern.compile("\\b(although|though|even if)\\b", Pattern.CASE_INSENSITIVE) to "让步",
        Pattern.compile("\\b(for example|for instance|such as)\\b", Pattern.CASE_INSENSITIVE) to "举例说明",
        Pattern.compile("\\b(first|second|finally|in conclusion|in short)\\b", Pattern.CASE_INSENSITIVE) to "顺序与总结",
        Pattern.compile("\\b(if|unless|when)\\b", Pattern.CASE_INSENSITIVE) to "条件时间",
        Pattern.compile("\\b(and|also|besides)\\b", Pattern.CASE_INSENSITIVE) to "并列递进",
    )

    fun analyzeHeuristic(text: String): ReadingAnalysis? {
        val t = text.trim()
        if (t.length < 15) return null
        val paras = splitParagraphs(t)
        val gists = paras.mapIndexed { i, p ->
            ParagraphGist(
                index = i + 1,
                gistZh = "（启发式）以下为首句/关键句参考，完整段意请结合全段核对；印刷或 OCR 可能有误差。",
                keySentenceEn = firstSentence(p)
            )
        }
        var logic = detectLogic(t)
        if (logic.isEmpty()) {
            logic = listOf("（启发式）未检出明显路标词时，仍留意 and / but / because 等基础逻辑连接词。")
        }
        return ReadingAnalysis(
            mode = "heuristic",
            paragraphGists = gists,
            coreViewpointZh = "（未使用 AI）请通读各段首句、末段与反复出现的主题词，用一句话概括「作者最想传递的信息」；说明文常为「对象 + 特征/步骤」，议论文关注论点与论据。",
            examPoints = detectExamHints(t),
            logicRelations = logic,
            howToSolveZh = staticHowToSolve
        )
    }

    fun parseAIResponse(raw: String): ReadingAnalysis? {
        var s = raw.trim()
        if (s.startsWith("```")) {
            val firstNl = s.indexOf('\n')
            if (firstNl >= 0) s = s.substring(firstNl + 1).trim()
            val idx = s.lastIndexOf("```")
            if (idx >= 0) s = s.substring(0, idx).trim()
        }
        return runCatching {
            val obj = JSONObject(s)
            val pg = obj.optJSONArray("paragraphGists") ?: JSONArray()
            val gists = buildList {
                for (i in 0 until pg.length()) {
                    val item = pg.optJSONObject(i) ?: continue
                    add(
                        ParagraphGist(
                            index = item.optInt("index", size + 1),
                            gistZh = item.optString("gistZh", "").trim(),
                            keySentenceEn = item.optString("keySentenceEn", "").trim()
                        )
                    )
                }
            }
            val core = obj.optString("coreViewpointZh", "").trim()
            val exam = obj.optJSONArray("examPoints")?.toStringList() ?: emptyList()
            val logic = obj.optJSONArray("logicRelations")?.toStringList() ?: emptyList()
            val how = obj.optString("howToSolveZh", "").trim()
            if (gists.isEmpty() && core.isEmpty() && exam.isEmpty() && logic.isEmpty() && how.isEmpty()) return@runCatching null
            ReadingAnalysis(
                mode = "ai",
                paragraphGists = gists,
                coreViewpointZh = core,
                examPoints = exam,
                logicRelations = logic,
                howToSolveZh = how
            )
        }.getOrNull()
    }

    private fun JSONArray.toStringList(): List<String> = buildList {
        for (i in 0 until length()) {
            val v = opt(i)
            if (v is String && v.isNotBlank()) add(v.trim())
        }
    }

    private fun splitParagraphs(text: String): List<String> {
        var parts = text.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.size <= 1 && text.length > 380) {
            val sentenceRe = Pattern.compile("[^.!?]+[.!?]+")
            val m = sentenceRe.matcher(text)
            val sentences = mutableListOf<String>()
            while (m.find()) sentences.add(m.group())
            if (sentences.isNotEmpty()) {
                val chunks = mutableListOf<String>()
                var i = 0
                while (i < sentences.size) {
                    val end = minOf(i + 3, sentences.size)
                    chunks.add(sentences.subList(i, end).joinToString(" "))
                    i += 3
                }
                if (chunks.size > 1) parts = chunks
            }
        }
        return if (parts.isEmpty()) listOf(text) else parts
    }

    private fun firstSentence(p: String): String {
        val s = p.trim()
        val re = Pattern.compile("^[\\s\\S]{1,400}?[.!?](?=\\s|$)")
        val m = re.matcher(s)
        if (m.find()) return m.group()
        val line = s.lineSequence().firstOrNull() ?: s
        return line.take(220)
    }

    private fun detectLogic(text: String): List<String> {
        val found = linkedSetOf<String>()
        for ((re, zh) in logicPatterns) {
            if (re.matcher(text).find()) found.add(zh)
        }
        return found.map { "文中可体现「$it」类逻辑衔接，做题时注意选项是否与该逻辑一致。" }
    }

    private fun detectExamHints(text: String): List<String> {
        val rules = listOf(
            Pattern.compile("\\b(according to the passage|the passage says|paragraph\\s*\\d)\\b", Pattern.CASE_INSENSITIVE) to
                "信息定位题：用题干关键词回原文找依据，排除未提及或偷换概念的选项。",
            Pattern.compile("\\b(main(ly)?\\s+idea|best title|purpose of|mainly about)\\b", Pattern.CASE_INSENSITIVE) to
                "主旨/标题类：串联各段中心句，勿把某一例子或细节当作全文中心。",
            Pattern.compile("\\b(infer|imply|suggest|it can be learned)\\b", Pattern.CASE_INSENSITIVE) to
                "推断题：选项必须是原文可合理推出的结论，忌绝对化与无中生有。",
            Pattern.compile("\\b(the word|refers to|closest in meaning)\\b", Pattern.CASE_INSENSITIVE) to
                "词义/指代题：看前后句同义复述、举例或反义对比；指代向前找最近名词。",
            Pattern.compile("\\b(order|sequence|correct order)\\b", Pattern.CASE_INSENSITIVE) to
                "排序题：抓住时间词与 first/then/finally 等路标，先定位再排顺序。"
        )
        val out = mutableListOf<String>()
        for ((pat, msg) in rules) {
            if (pat.matcher(text).find()) out.add(msg)
        }
        val extra = listOf(
            "长难句：先找主谓宾，再看从句与插入语，避免被生词干扰整体理解。",
            "对比题：注意 while、whereas、unlike 等引出的对照关系。"
        )
        for (e in extra) {
            if (out.size < 8) out.add(e)
        }
        return out.distinct().take(8)
    }
}
