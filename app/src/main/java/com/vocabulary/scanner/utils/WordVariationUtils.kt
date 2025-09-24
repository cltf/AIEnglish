package com.vocabulary.scanner.utils

/**
 * 单词变形处理工具类
 * 处理时态、单复数、人称等因素导致的单词变形
 */
object WordVariationUtils {
    
    /**
     * 检查单词是否在词汇库中（考虑变形）
     * @param word 要检查的单词
     * @param vocabularySet 词汇库
     * @return 如果找到匹配的词汇则返回原形，否则返回null
     */
    fun findWordInVocabulary(word: String, vocabularySet: Set<String>): String? {
        val lowerWord = word.lowercase()
        
        // 1. 直接匹配
        if (vocabularySet.contains(lowerWord)) {
            return lowerWord
        }
        
        // 2. 处理名词所有格
        val possessiveBase = getPossessiveBaseForm(lowerWord)
        if (possessiveBase != null && vocabularySet.contains(possessiveBase)) {
            return possessiveBase
        }
        
        // 3. 处理复数形式
        val singularForm = getSingularForm(lowerWord)
        if (singularForm != null && vocabularySet.contains(singularForm)) {
            return singularForm
        }
        
        // 4. 处理动词时态
        val baseForm = getVerbBaseForm(lowerWord)
        if (baseForm != null && vocabularySet.contains(baseForm)) {
            return baseForm
        }
        
        // 5. 处理形容词比较级和最高级
        val adjectiveBase = getAdjectiveBaseForm(lowerWord)
        if (adjectiveBase != null && vocabularySet.contains(adjectiveBase)) {
            return adjectiveBase
        }
        
        // 6. 处理不规则变形
        val irregularBase = getIrregularBaseForm(lowerWord)
        if (irregularBase != null && vocabularySet.contains(irregularBase)) {
            return irregularBase
        }
        
        return null
    }
    
    /**
     * 获取名词的单数形式
     */
    private fun getSingularForm(word: String): String? {
        // 处理常见的复数形式
        when {
            word.endsWith("ies") && word.length > 3 -> {
                // babies -> baby, cities -> city
                return word.dropLast(3) + "y"
            }
            word.endsWith("ves") && word.length > 3 -> {
                // knives -> knife, leaves -> leaf
                val base = word.dropLast(3)
                return when {
                    base.endsWith("f") -> base.dropLast(1) + "fe"
                    base.endsWith("e") -> base + "f"
                    else -> {
                        // 对于knives -> knife这种情况，特殊处理
                        when (base) {
                            "kni" -> "knife"
                            "lea" -> "leaf"
                            else -> base + "e"
                        }
                    }
                }
            }
            word.endsWith("es") && word.length > 2 -> {
                // boxes -> box, classes -> class
                val base = word.dropLast(2)
                return when {
                    base.endsWith("s") || base.endsWith("sh") || base.endsWith("ch") || 
                    base.endsWith("x") || base.endsWith("z") -> base
                    base.endsWith("o") -> base
                    else -> {
                        // 如果es结尾但不满足特殊条件，尝试去掉s
                        word.dropLast(1)
                    }
                }
            }
            word.endsWith("s") && word.length > 1 -> {
                // cats -> cat, dogs -> dog, homes -> home
                return word.dropLast(1)
            }
        }
        return null
    }
    
    /**
     * 获取动词的原形
     */
    private fun getVerbBaseForm(word: String): String? {
        // 处理第三人称单数
        if (word.endsWith("ies") && word.length > 3) {
            // studies -> study, tries -> try
            return word.dropLast(3) + "y"
        }
        if (word.endsWith("es") && word.length > 2) {
            // goes -> go, does -> do, watches -> watch, comes -> come, sees -> see
            val base = word.dropLast(2)
            return when {
                base.endsWith("s") || base.endsWith("sh") || base.endsWith("ch") || 
                base.endsWith("x") || base.endsWith("z") -> base
                base.endsWith("o") -> base
                base.endsWith("e") -> base // comes -> come, sees -> see
                else -> null
            }
        }
        if (word.endsWith("s") && word.length > 1) {
            // runs -> run, plays -> play
            return word.dropLast(1)
        }
        
        // 处理过去式和过去分词
        if (word.endsWith("ed") && word.length > 2) {
            // played -> play, studied -> study
            val base = word.dropLast(2)
            return when {
                base.endsWith("i") -> base.dropLast(1) + "y" // studied -> study
                base.endsWith("e") -> base // liked -> like
                else -> {
                    // 对于liked -> like这种情况，特殊处理
                    if (base == "lik") "like" else base
                }
            }
        }
        
        // 处理现在分词
        if (word.endsWith("ing") && word.length > 3) {
            // playing -> play, studying -> study
            val base = word.dropLast(3)
            return when {
                base.endsWith("i") -> base.dropLast(1) + "y" // studying -> study
                base.endsWith("e") -> base // liking -> like
                else -> {
                    // 对于liking -> like这种情况，特殊处理
                    if (base == "lik") "like" else base
                }
            }
        }
        
        return null
    }
    
    /**
     * 获取形容词的原形
     */
    private fun getAdjectiveBaseForm(word: String): String? {
        // 处理比较级
        if (word.endsWith("er") && word.length > 2) {
            // bigger -> big, happier -> happy
            val base = word.dropLast(2)
            return when {
                base.endsWith("i") -> base.dropLast(1) + "y" // happier -> happy
                else -> {
                    // 对于bigger -> big这种情况，处理双辅音
                    if (base.length > 1 && base.last() == base[base.length - 2] && base != "small") {
                        base.dropLast(1) // 去掉重复的辅音
                    } else base
                }
            }
        }
        
        // 处理最高级
        if (word.endsWith("est") && word.length > 3) {
            // biggest -> big, happiest -> happy
            val base = word.dropLast(3)
            return when {
                base.endsWith("i") -> base.dropLast(1) + "y" // happiest -> happy
                else -> {
                    // 对于biggest -> big这种情况，处理双辅音
                    if (base.length > 1 && base.last() == base[base.length - 2] && base != "small") {
                        base.dropLast(1) // 去掉重复的辅音
                    } else base
                }
            }
        }
        
        return null
    }
    
    /**
     * 处理名词所有格
     */
    private fun getPossessiveBaseForm(word: String): String? {
        if (word.endsWith("'s") && word.length > 2) {
            // Tom's -> Tom, cat's -> cat
            return word.dropLast(2)
        }
        if (word.endsWith("s'") && word.length > 2) {
            // 对于复数所有格，返回复数形式
            return word.dropLast(1)
        }
        return null
    }
    
    /**
     * 处理不规则变形
     */
    private fun getIrregularBaseForm(word: String): String? {
        // 常见的不规则变形映射
        val irregularMap = mapOf(
            // 不规则复数
            "children" to "child",
            "men" to "man",
            "women" to "woman",
            "feet" to "foot",
            "teeth" to "tooth",
            "mice" to "mouse",
            "geese" to "goose",
            "oxen" to "ox",
            
            // 不规则动词
            "went" to "go",
            "gone" to "go",
            "came" to "come",
            "come" to "come",
            "saw" to "see",
            "seen" to "see",
            "did" to "do",
            "done" to "do",
            "had" to "have",
            "has" to "have",
            "was" to "be",
            "were" to "be",
            "been" to "be",
            "being" to "be",
            "am" to "be",
            "is" to "be",
            "are" to "be",
            "got" to "get",
            "gotten" to "get",
            "took" to "take",
            "taken" to "take",
            "made" to "make",
            "gave" to "give",
            "given" to "give",
            "said" to "say",
            "told" to "tell",
            "knew" to "know",
            "known" to "know",
            "thought" to "think",
            "brought" to "bring",
            "bought" to "buy",
            "caught" to "catch",
            "taught" to "teach",
            "fought" to "fight",
            "sought" to "seek",
            "found" to "find",
            "built" to "build",
            "sent" to "send",
            "spent" to "spend",
            "bent" to "bend",
            "lent" to "lend",
            "meant" to "mean",
            "kept" to "keep",
            "slept" to "sleep",
            "swept" to "sweep",
            "wept" to "weep",
            "crept" to "creep",
            "leapt" to "leap",
            "felt" to "feel",
            "dealt" to "deal",
            "left" to "leave",
            "lost" to "lose",
            "won" to "win",
            "began" to "begin",
            "begun" to "begin",
            "drank" to "drink",
            "drunk" to "drink",
            "rang" to "ring",
            "rung" to "ring",
            "sang" to "sing",
            "sung" to "sing",
            "sank" to "sink",
            "sunk" to "sink",
            "swam" to "swim",
            "swum" to "swim",
            "ran" to "run",
            "shook" to "shake",
            "shaken" to "shake",
            "stole" to "steal",
            "stolen" to "steal",
            "broke" to "break",
            "broken" to "break",
            "chose" to "choose",
            "chosen" to "choose",
            "spoke" to "speak",
            "spoken" to "speak",
            "woke" to "wake",
            "woken" to "wake",
            "wore" to "wear",
            "worn" to "wear",
            "tore" to "tear",
            "torn" to "tear",
            "bore" to "bear",
            "born" to "bear",
            "swore" to "swear",
            "sworn" to "swear",
            "froze" to "freeze",
            "frozen" to "freeze",
            "drove" to "drive",
            "driven" to "drive",
            "wrote" to "write",
            "written" to "write",
            "rode" to "ride",
            "ridden" to "ride",
            "rose" to "rise",
            "risen" to "rise",
            "fell" to "fall",
            "fallen" to "fall",
            "held" to "hold",
            "stood" to "stand",
            "understood" to "understand",
            "misunderstood" to "misunderstand",
            "withstood" to "withstand",
            "withheld" to "withhold",
            "overcame" to "overcome",
            "became" to "become",
            "came" to "come",
            "overcame" to "overcome",
            "overcame" to "overcome",
            
            // 不规则形容词
            "better" to "good",
            "best" to "good",
            "worse" to "bad",
            "worst" to "bad",
            "more" to "much",
            "most" to "much",
            "less" to "little",
            "least" to "little",
            "farther" to "far",
            "farthest" to "far",
            "further" to "far",
            "furthest" to "far",
            "older" to "old",
            "oldest" to "old",
            "elder" to "old",
            "eldest" to "old"
        )
        
        return irregularMap[word]
    }
}

