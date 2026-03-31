import Foundation

struct VocabularyFile: Codable {
    var version: Int?
    var words: [WordRecord]
}

struct WordRecord: Codable, Identifiable, Hashable {
    var id: String { word }
    let word: String
    let phonetic: String
    let type: String
    let definitions: [DefinitionItem]
    let example: String?
    let exampleZh: String?

    enum CodingKeys: String, CodingKey {
        case word, phonetic, type, definitions, example, exampleZh
    }
}

struct DefinitionItem: Codable, Hashable {
    let partOfSpeech: String
    let meaning: String
}
