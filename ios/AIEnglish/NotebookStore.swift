import Foundation

/// 生词本：单词（小写）唯一，记录累计加入次数。存储为 JSON 对象 `{"word": count}`。
final class NotebookStore: ObservableObject {
    @Published private(set) var counts: [String: Int] = [:]
    /// 加入生词本成功提示，约 2 秒后自动清空
    @Published var toastMessage: String?
    private let key = "aienglish_notebook"
    private var toastWorkItem: DispatchWorkItem?

    /// 按字母序，用于列表
    var entries: [(word: String, count: Int)] {
        counts.map { (word: $0.key, count: $0.value) }.sorted { $0.word < $1.word }
    }

    init() { load() }

    func load() {
        guard let data = UserDefaults.standard.data(forKey: key) else {
            counts = [:]
            return
        }
        if let dict = try? JSONDecoder().decode([String: Int].self, from: data) {
            counts = dict.filter { $0.value > 0 }
            return
        }
        if let arr = try? JSONDecoder().decode([String].self, from: data) {
            var m: [String: Int] = [:]
            for w in arr {
                m[w.lowercased(), default: 0] = 1
            }
            counts = m
            save()
            return
        }
        counts = [:]
    }

    func save() {
        if let data = try? JSONEncoder().encode(counts) {
            UserDefaults.standard.set(data, forKey: key)
        }
    }

    func add(_ word: String) {
        let w = word.lowercased()
        guard !w.isEmpty else { return }
        counts[w, default: 0] += 1
        save()
        toastMessage = "加入生词本成功"
        toastWorkItem?.cancel()
        let work = DispatchWorkItem { [weak self] in
            self?.toastMessage = nil
        }
        toastWorkItem = work
        DispatchQueue.main.asyncAfter(deadline: .now() + 2, execute: work)
    }

    func remove(_ word: String) {
        let w = word.lowercased()
        counts.removeValue(forKey: w)
        save()
    }

    func contains(_ word: String) -> Bool {
        (counts[word.lowercased()] ?? 0) > 0
    }

    func count(for word: String) -> Int {
        counts[word.lowercased(), default: 0]
    }

    func clear() {
        counts = [:]
        save()
    }
}
