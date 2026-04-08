import Foundation

struct EssaysPayload: Codable {
    let version: Int
    let exams: [EssayExam]
}

struct EssayExam: Codable, Identifiable, Hashable {
    let id: String
    let title: String
    let topics: String
    let samples: [EssaySample]
    /// english：北京中考英语作文；chinese：语文作文
    let subject: String

    enum CodingKeys: String, CodingKey {
        case id, title, topics, samples, subject
    }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: CodingKeys.self)
        id = try c.decode(String.self, forKey: .id)
        title = try c.decode(String.self, forKey: .title)
        topics = try c.decode(String.self, forKey: .topics)
        samples = try c.decode([EssaySample].self, forKey: .samples)
        subject = try c.decodeIfPresent(String.self, forKey: .subject) ?? "english"
    }

    func encode(to encoder: Encoder) throws {
        var c = encoder.container(keyedBy: CodingKeys.self)
        try c.encode(id, forKey: .id)
        try c.encode(title, forKey: .title)
        try c.encode(topics, forKey: .topics)
        try c.encode(samples, forKey: .samples)
        try c.encode(subject, forKey: .subject)
    }
}

struct EssaySample: Codable, Identifiable, Hashable {
    let id: String
    let title: String
    let body: String
}

enum EssayDataLoader {
    static func load() throws -> [EssayExam] {
        guard let url = Bundle.main.url(forResource: "essays", withExtension: "json") else {
            throw NSError(domain: "AIEnglish", code: 1, userInfo: [NSLocalizedDescriptionKey: "未找到 essays.json"])
        }
        let data = try Data(contentsOf: url)
        let payload = try JSONDecoder().decode(EssaysPayload.self, from: data)
        return payload.exams
    }
}
