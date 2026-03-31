import Foundation

final class NotebookStore: ObservableObject {
    @Published private(set) var words: [String] = []
    private let key = "aienglish_notebook"

    init() { load() }

    func load() {
        if let data = UserDefaults.standard.data(forKey: key),
           let arr = try? JSONDecoder().decode([String].self, from: data) {
            words = Array(Set(arr.map { $0.lowercased() })).sorted()
        } else {
            words = []
        }
    }

    func save() {
        if let data = try? JSONEncoder().encode(words) {
            UserDefaults.standard.set(data, forKey: key)
        }
    }

    func add(_ word: String) {
        let w = word.lowercased()
        guard !words.contains(w) else { return }
        words.append(w)
        words.sort()
        save()
    }

    func remove(_ word: String) {
        let w = word.lowercased()
        words.removeAll { $0 == w }
        save()
    }

    func contains(_ word: String) -> Bool {
        words.contains(word.lowercased())
    }

    func clear() {
        words = []
        save()
    }
}
