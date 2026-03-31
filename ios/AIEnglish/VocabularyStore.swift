import Foundation
import SwiftUI

@MainActor
final class VocabularyStore: ObservableObject {
    @Published private(set) var words: [WordRecord] = []
    private var wordByKey: [String: WordRecord] = [:]
    private(set) var vocabularyKeys: Set<String> = []

    init() {
        loadFromBundle()
    }

    func loadFromBundle() {
        guard let url = Bundle.main.url(forResource: "vocabulary", withExtension: "json")
                ?? Bundle.main.url(forResource: "vocabulary", withExtension: "json", subdirectory: "web/data") else {
            return
        }
        do {
            let data = try Data(contentsOf: url)
            let file = try JSONDecoder().decode(VocabularyFile.self, from: data)
            applyWords(file.words)
        } catch {
            print("Vocabulary load error:", error)
        }
    }

    private func applyWords(_ list: [WordRecord]) {
        wordByKey = [:]
        vocabularyKeys = []
        for w in list {
            let key = w.word.lowercased()
            wordByKey[key] = w
            vocabularyKeys.insert(key)
        }
        words = list.sorted { $0.word.localizedCompare($1.word) == .orderedAscending }
    }

    func record(for word: String) -> WordRecord? {
        wordByKey[word.lowercased()]
    }

    func resolve(_ token: String) -> String? {
        WordVariation.findWordInVocabulary(token, vocabularySet: vocabularyKeys)
    }

    func listFiltered(type: String, search: String) -> [WordRecord] {
        let q = search.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        return words.filter { rec in
            if type != "ALL", rec.type != type { return false }
            if !q.isEmpty, !rec.word.lowercased().contains(q) { return false }
            return true
        }
    }

    var totalCount: Int { wordByKey.count }
}
