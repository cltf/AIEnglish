import Foundation

struct ReadingAnalysis {
    var mode: String
    var paragraphGists: [(index: Int, gistZh: String, keySentenceEn: String)]
    var coreViewpointZh: String
    var examPoints: [String]
    var logicRelations: [String]
    var howToSolveZh: String
}

enum ReadingAnalysisEngine {
    private static let staticHowToSolve = """
1. 先读题干与选项，圈出题干关键词（人名、地名、数字、大写、段落号）。
2. 细节题用关键词回原文定位，同义改写多为正确选项。
3. 主旨/标题题重点读首段、尾段与各段首句，排除片面细节项。
4. 词义猜测题看该词前后句的同义复述、举例或对比线索。
5. 推断题只选「能从原文合理推出」的选项，忌主观臆断与过度引申。
6. 注意路标词：转折、因果、递进、举例，常对应考点与正确项。
"""

    private static let logicPatterns: [(NSRegularExpression, String)] = {
        let pairs: [(String, String)] = [
            (#"\b(however|but|yet|instead|while)\b"#, "转折对比"),
            (#"\b(because|since|as|so that)\b"#, "原因目的"),
            (#"\b(therefore|thus|so|as a result|consequently)\b"#, "结果结论"),
            (#"\b(although|though|even if)\b"#, "让步"),
            (#"\b(for example|for instance|such as)\b"#, "举例说明"),
            (#"\b(first|second|finally|in conclusion|in short)\b"#, "顺序与总结"),
            (#"\b(if|unless|when)\b"#, "条件时间"),
            (#"\b(and|also|besides)\b"#, "并列递进"),
        ]
        return pairs.compactMap { pair in
            (try? NSRegularExpression(pattern: pair.0, options: .caseInsensitive)).map { ($0, pair.1) }
        }
    }()

    static func analyzeHeuristic(text: String) -> ReadingAnalysis? {
        let t = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard t.count >= 15 else { return nil }
        let paras = splitParagraphs(t)
        let gists: [(Int, String, String)] = paras.enumerated().map { i, p in
            (i + 1, "（启发式）以下为首句/关键句参考，完整段意请结合全段核对；印刷或 OCR 可能有误差。", firstSentence(p))
        }
        var logic = detectLogic(t)
        if logic.isEmpty {
            logic = ["（启发式）未检出明显路标词时，仍留意 and / but / because 等基础逻辑连接词。"]
        }
        return ReadingAnalysis(
            mode: "heuristic",
            paragraphGists: gists,
            coreViewpointZh: "（未使用 AI）请通读各段首句、末段与反复出现的主题词，用一句话概括「作者最想传递的信息」；说明文常为「对象 + 特征/步骤」，议论文关注论点与论据。",
            examPoints: detectExamHints(t),
            logicRelations: logic,
            howToSolveZh: staticHowToSolve
        )
    }

    private static func splitParagraphs(_ text: String) -> [String] {
        var parts = text.components(separatedBy: "\n\n").map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        if parts.count <= 1, text.count > 380 {
            let sentenceRe = try! NSRegularExpression(pattern: #"[^.!?]+[.!?]+"#, options: [])
            let range = NSRange(text.startIndex..., in: text)
            let matches = sentenceRe.matches(in: text, options: [], range: range)
            let sentences = matches.compactMap { m -> String? in
                guard let r = Range(m.range, in: text) else { return nil }
                return String(text[r])
            }
            if !sentences.isEmpty {
                var chunks: [String] = []
                var i = 0
                while i < sentences.count {
                    let slice = sentences[i..<min(i + 3, sentences.count)]
                    chunks.append(slice.joined(separator: " "))
                    i += 3
                }
                if chunks.count > 1 { parts = chunks }
            }
        }
        return parts.isEmpty ? [text] : parts
    }

    private static func firstSentence(_ p: String) -> String {
        let s = p.trimmingCharacters(in: .whitespacesAndNewlines)
        if let r = s.range(of: #"^[\s\S]{1,400}?[.!?](?=\s|$)"#, options: .regularExpression) {
            return String(s[r])
        }
        let line = s.split(separator: "\n", omittingEmptySubsequences: false).first.map(String.init) ?? s
        return String(line.prefix(220))
    }

    private static func detectLogic(_ text: String) -> [String] {
        var found = Set<String>()
        for (re, zh) in logicPatterns {
            let range = NSRange(text.startIndex..., in: text)
            if re.firstMatch(in: text, options: [], range: range) != nil {
                found.insert(zh)
            }
        }
        return found.map { "文中可体现「\($0)」类逻辑衔接，做题时注意选项是否与该逻辑一致。" }
    }

    private static func detectExamHints(_ text: String) -> [String] {
        var out: [String] = []
        let rules: [(String, String)] = [
            (#"\b(according to the passage|the passage says|paragraph\s*\d)\b"#, "信息定位题：用题干关键词回原文找依据，排除未提及或偷换概念的选项。"),
            (#"\b(main(ly)?\s+idea|best title|purpose of|mainly about)\b"#, "主旨/标题类：串联各段中心句，勿把某一例子或细节当作全文中心。"),
            (#"\b(infer|imply|suggest|it can be learned)\b"#, "推断题：选项必须是原文可合理推出的结论，忌绝对化与无中生有。"),
            (#"\b(the word|refers to|closest in meaning)\b"#, "词义/指代题：看前后句同义复述、举例或反义对比；指代向前找最近名词。"),
            (#"\b(order|sequence|correct order)\b"#, "排序题：抓住时间词与 first/then/finally 等路标，先定位再排顺序。"),
        ]
        for (pat, msg) in rules {
            if (try? NSRegularExpression(pattern: pat, options: .caseInsensitive))?.firstMatch(
                in: text, options: [], range: NSRange(text.startIndex..., in: text)
            ) != nil {
                out.append(msg)
            }
        }
        let extra = [
            "长难句：先找主谓宾，再看从句与插入语，避免被生词干扰整体理解。",
            "对比题：注意 while、whereas、unlike 等引出的对照关系。",
        ]
        for e in extra where out.count < 8 { out.append(e) }
        return Array(Set(out)).prefix(8).map { $0 }
    }

    /// 解析 `AIService.analyzeReading` 返回的 JSON 字符串（可含 markdown 围栏）。
    static func parseAIResponse(_ raw: String) -> ReadingAnalysis? {
        var s = raw.trimmingCharacters(in: .whitespacesAndNewlines)
        if s.hasPrefix("```") {
            if let firstNl = s.firstIndex(of: "\n") {
                s = String(s[s.index(after: firstNl)...]).trimmingCharacters(in: .whitespacesAndNewlines)
            }
            if let range = s.range(of: "```", options: .backwards) {
                s = String(s[..<range.lowerBound]).trimmingCharacters(in: .whitespacesAndNewlines)
            }
        }
        guard let data = s.data(using: .utf8),
              let obj = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else { return nil }
        let pg = obj["paragraphGists"] as? [[String: Any]] ?? []
        var gists: [(Int, String, String)] = []
        for item in pg {
            let idx = item["index"] as? Int ?? (gists.count + 1)
            let g = (item["gistZh"] as? String)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
            let k = (item["keySentenceEn"] as? String)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
            gists.append((idx, g, k))
        }
        let core = (obj["coreViewpointZh"] as? String)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        let exam = (obj["examPoints"] as? [Any])?.compactMap { $0 as? String } ?? []
        let logic = (obj["logicRelations"] as? [Any])?.compactMap { $0 as? String } ?? []
        let how = (obj["howToSolveZh"] as? String)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        if gists.isEmpty && core.isEmpty && exam.isEmpty && logic.isEmpty && how.isEmpty { return nil }
        return ReadingAnalysis(
            mode: "ai",
            paragraphGists: gists,
            coreViewpointZh: core,
            examPoints: exam,
            logicRelations: logic,
            howToSolveZh: how
        )
    }
}
