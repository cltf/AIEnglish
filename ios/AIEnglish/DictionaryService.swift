import Foundation

enum DictionaryService {
    static func fetchExample(for word: String) async -> String? {
        let w = word.lowercased().trimmingCharacters(in: .whitespacesAndNewlines)
        guard !w.isEmpty else { return nil }
        let slugs = w.contains(" ") ? [w, w.split(separator: " ").first.map(String.init) ?? w] : [w]
        for slug in slugs {
            guard let url = URL(string: "https://api.dictionaryapi.dev/api/v2/entries/en/\(slug.addingPercentEncoding(withAllowedCharacters: .urlPathAllowed) ?? slug)") else { continue }
            do {
                let (data, resp) = try await URLSession.shared.data(from: url)
                guard let http = resp as? HTTPURLResponse, http.statusCode == 200 else { continue }
                let arr = try JSONSerialization.jsonObject(with: data) as? [[String: Any]] ?? []
                if let ex = extractExample(from: arr, word: slug) {
                    return ex.hasSuffix(".") || ex.hasSuffix("!") || ex.hasSuffix("?") ? ex : ex + "."
                }
            } catch {
                continue
            }
        }
        return nil
    }

    private static func extractExample(from entries: [[String: Any]], word: String) -> String? {
        var candidates: [String] = []
        for entry in entries {
            guard let meanings = entry["meanings"] as? [[String: Any]] else { continue }
            for m in meanings {
                guard let defs = m["definitions"] as? [[String: Any]] else { continue }
                for d in defs {
                    if let ex = d["example"] as? String, !ex.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        candidates.append(ex.trimmingCharacters(in: .whitespacesAndNewlines))
                    }
                }
            }
        }
        guard !candidates.isEmpty else { return nil }
        let escaped = NSRegularExpression.escapedPattern(for: word)
        let boundary = try? NSRegularExpression(pattern: "\\b\(escaped)\\b", options: .caseInsensitive)
        let full = NSRange(location: 0, length: word.utf16.count)
        for ex in candidates {
            let ns = ex as NSString
            let range = NSRange(location: 0, length: ns.length)
            if boundary?.firstMatch(in: ex, options: [], range: range) != nil { return ex }
        }
        return candidates.first
    }

    static func translateToZh(_ english: String) async -> String? {
        let t = english.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !t.isEmpty else { return nil }
        guard let q = t.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed),
              let url = URL(string: "https://api.mymemory.translated.net/get?q=\(q)&langpair=en|zh-CN") else { return nil }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let obj = try JSONSerialization.jsonObject(with: data) as? [String: Any]
            let rd = obj?["responseData"] as? [String: Any]
            let translated = rd?["translatedText"] as? String
            return translated?.trimmingCharacters(in: .whitespacesAndNewlines).nilIfEmpty
        } catch {
            return nil
        }
    }
}

private extension String {
    var nilIfEmpty: String? { isEmpty ? nil : self }
}
