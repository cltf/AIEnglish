import Foundation

/// 与 web/js/word-variation.js、Android WordVariationUtils 对齐
enum WordVariation {
    private static let irregularMap: [String: String] = [
        "children": "child", "men": "man", "women": "woman", "feet": "foot", "teeth": "tooth",
        "mice": "mouse", "geese": "goose", "oxen": "ox", "went": "go", "gone": "go", "came": "come",
        "saw": "see", "seen": "see", "did": "do", "done": "do", "had": "have", "has": "have",
        "was": "be", "were": "be", "been": "be", "being": "be", "am": "be", "is": "be", "are": "be",
        "got": "get", "gotten": "get", "took": "take", "taken": "take", "made": "make", "gave": "give",
        "given": "give", "said": "say", "told": "tell", "knew": "know", "known": "know",
        "thought": "think", "brought": "bring", "bought": "buy", "caught": "catch", "taught": "teach",
        "fought": "fight", "sought": "seek", "found": "find", "built": "build", "sent": "send",
        "spent": "spend", "bent": "bend", "lent": "lend", "meant": "mean", "kept": "keep",
        "slept": "sleep", "swept": "sweep", "wept": "weep", "crept": "creep", "leapt": "leap",
        "felt": "feel", "dealt": "deal", "left": "leave", "lost": "lose", "won": "win",
        "began": "begin", "begun": "begin", "drank": "drink", "drunk": "drink", "rang": "ring",
        "rung": "ring", "sang": "sing", "sung": "sing", "sank": "sink", "sunk": "sink",
        "swam": "swim", "swum": "swim", "ran": "run", "shook": "shake", "shaken": "shake",
        "stole": "steal", "stolen": "steal", "broke": "break", "broken": "break", "chose": "choose",
        "chosen": "choose", "spoke": "speak", "spoken": "speak", "woke": "wake", "woken": "wake",
        "wore": "wear", "worn": "wear", "tore": "tear", "torn": "tear", "bore": "bear", "born": "bear",
        "swore": "swear", "sworn": "swear", "froze": "freeze", "frozen": "freeze", "drove": "drive",
        "driven": "drive", "wrote": "write", "written": "write", "rode": "ride", "ridden": "ride",
        "rose": "rise", "risen": "rise", "fell": "fall", "fallen": "fall", "held": "hold",
        "stood": "stand", "understood": "understand", "misunderstood": "misunderstand",
        "withstood": "withstand", "withheld": "withhold", "overcame": "overcome", "became": "become",
        "better": "good", "best": "good", "worse": "bad", "worst": "bad", "more": "much", "most": "much",
        "less": "little", "least": "little", "farther": "far", "farthest": "far", "further": "far",
        "furthest": "far", "older": "old", "oldest": "old", "elder": "old", "eldest": "old",
    ]

    static func findWordInVocabulary(_ word: String, vocabularySet: Set<String>) -> String? {
        let lower = word.lowercased()
        if vocabularySet.contains(lower) { return lower }
        if let p = possessiveBase(lower), vocabularySet.contains(p) { return p }
        if let s = singularForm(lower), vocabularySet.contains(s) { return s }
        if let v = verbBase(lower), vocabularySet.contains(v) { return v }
        if let a = adjectiveBase(lower), vocabularySet.contains(a) { return a }
        if let i = irregularMap[lower], vocabularySet.contains(i) { return i }
        return nil
    }

    private static func possessiveBase(_ word: String) -> String? {
        if word.hasSuffix("'s"), word.count > 2 { return String(word.dropLast(2)) }
        if word.hasSuffix("s'"), word.count > 2 { return String(word.dropLast(2)) }
        return nil
    }

    private static func singularForm(_ word: String) -> String? {
        if word.hasSuffix("ies"), word.count > 3 { return String(word.dropLast(3)) + "y" }
        if word.hasSuffix("ves"), word.count > 3 {
            let base = String(word.dropLast(3))
            if base.hasSuffix("f") { return String(base.dropLast()) + "fe" }
            if base.hasSuffix("e") { return base + "f" }
            if base == "kni" { return "knife" }
            if base == "lea" { return "leaf" }
            return base + "e"
        }
        if word.hasSuffix("es"), word.count > 2 {
            let base = String(word.dropLast(2))
            if base.range(of: "[sxz]$", options: .regularExpression) != nil { return base }
            if base.range(of: "(sh|ch)$", options: .regularExpression) != nil { return base }
            if base.hasSuffix("o") { return base }
            return String(word.dropLast())
        }
        if word.hasSuffix("s"), word.count > 1 { return String(word.dropLast()) }
        return nil
    }

    private static func verbBase(_ word: String) -> String? {
        if word.hasSuffix("ies"), word.count > 3 { return String(word.dropLast(3)) + "y" }
        if word.hasSuffix("es"), word.count > 2 {
            let base = String(word.dropLast(2))
            if base.range(of: "[sxz]$", options: .regularExpression) != nil { return base }
            if base.hasSuffix("o") { return base }
            if base.hasSuffix("e") { return base }
            return nil
        }
        if word.hasSuffix("s"), word.count > 1 { return String(word.dropLast()) }
        if word.hasSuffix("ed"), word.count > 2 {
            var base = String(word.dropLast(2))
            if base.hasSuffix("i") { return String(base.dropLast()) + "y" }
            if base.hasSuffix("e") { return base }
            if base == "lik" { return "like" }
            return base
        }
        if word.hasSuffix("ing"), word.count > 3 {
            var base = String(word.dropLast(3))
            if base.hasSuffix("i") { return String(base.dropLast()) + "y" }
            if base.hasSuffix("e") { return base }
            if base == "lik" { return "like" }
            return base
        }
        return nil
    }

    private static func adjectiveBase(_ word: String) -> String? {
        if word.hasSuffix("er"), word.count > 2 {
            var base = String(word.dropLast(2))
            if base.hasSuffix("i") { return String(base.dropLast()) + "y" }
            if base.count > 1, base.last == base.dropLast().last, base != "small" { return String(base.dropLast()) }
            return base
        }
        if word.hasSuffix("est"), word.count > 3 {
            var base = String(word.dropLast(3))
            if base.hasSuffix("i") { return String(base.dropLast()) + "y" }
            if base.count > 1, base.last == base.dropLast().last, base != "small" { return String(base.dropLast()) }
            return base
        }
        return nil
    }
}
