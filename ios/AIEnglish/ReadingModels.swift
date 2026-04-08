import Foundation

struct ReadingContentPayload: Codable {
    let version: Int
    let subjects: [ReadingSubject]
}

struct ReadingSubject: Codable, Identifiable, Hashable {
    let id: String
    let label: String
    let packs: [ReadingPack]
}

struct ReadingPack: Codable, Identifiable, Hashable {
    let id: String
    let title: String
    let sections: [ReadingSection]
    let footer: String?
}

struct ReadingSection: Codable, Identifiable, Hashable {
    let id: String
    let headline: String
    let body: String
}

enum ReadingDataLoader {
    static func load() throws -> [ReadingSubject] {
        guard let url = Bundle.main.url(forResource: "reading_content", withExtension: "json") else {
            throw NSError(domain: "AIEnglish", code: 1, userInfo: [NSLocalizedDescriptionKey: "未找到 reading_content.json"])
        }
        let data = try Data(contentsOf: url)
        let payload = try JSONDecoder().decode(ReadingContentPayload.self, from: data)
        return payload.subjects
    }
}
