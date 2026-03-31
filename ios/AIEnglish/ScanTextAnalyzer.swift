import Foundation

enum ScanTextAnalyzer {
    static func unknownWords(in text: String, vocabularyKeys: Set<String>) -> [String] {
        let re = try! NSRegularExpression(pattern: #"\b[a-zA-Z]+\b"#, options: [])
        let range = NSRange(text.startIndex..., in: text)
        let matches = re.matches(in: text, options: [], range: range)
        var seen = Set<String>()
        var out: [String] = []
        for m in matches {
            guard let r = Range(m.range, in: text) else { continue }
            let raw = String(text[r])
            let lower = raw.lowercased()
            guard lower.count > 2 else { continue }
            if WordVariation.findWordInVocabulary(lower, vocabularySet: vocabularyKeys) != nil {
                continue
            }
            if !seen.contains(lower) {
                seen.insert(lower)
                out.append(lower)
            }
        }
        return out
    }

    static func accuracyPercent(text: String, unknownCount: Int) -> Int {
        let words = text.trimmingCharacters(in: .whitespacesAndNewlines).split { $0.isWhitespace || $0.isNewline }
        let total = words.count
        guard total > 0 else { return 100 }
        let known = total - unknownCount
        return max(85, (known * 100) / total)
    }
}
