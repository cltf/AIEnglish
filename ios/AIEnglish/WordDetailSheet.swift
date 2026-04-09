import AVFoundation
import SwiftUI

enum WordDetailContext {
    case list, notebook, result
}

struct WordDetailSheet: View {
    let word: String
    var context: WordDetailContext = .list
    @EnvironmentObject private var vocabulary: VocabularyStore
    @EnvironmentObject private var notebook: NotebookStore
    @Environment(\.dismiss) private var dismiss
    @Environment(\.fontScale) private var fontScale
    @StateObject private var speaker = WordDetailSpeaker()
    @State private var exampleEn = ""
    @State private var exampleZh = ""
    @State private var loadingEx = false

    private var record: WordRecord? {
        WordDetailMerged.mergedRecord(base: vocabulary.record(for: word), word: word)
    }

    private var contextLabel: String {
        switch context {
        case .list: return "词库"
        case .notebook: return "生词本"
        case .result: return "阅读"
        }
    }

    private var typeBadge: String? {
        guard let t = record?.type else { return nil }
        return t == "ADVANCED" ? "超纲" : "中考"
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    heroCard
                    definitionsCard
                    exampleCard
                    notebookSection
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 28)
                .padding(.top, 8)
            }
            .background(Color(.systemGroupedBackground))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text("单词详情")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.secondary)
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title2)
                            .symbolRenderingMode(.hierarchical)
                            .foregroundStyle(.secondary, Color(.tertiarySystemFill))
                    }
                    .accessibilityLabel("关闭")
                }
            }
            .task {
                await loadExample()
            }
        }
    }

    // MARK: - Sections

    private var heroCard: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack(alignment: .center, spacing: 10) {
                Text(word.lowercased())
                    .font(.system(size: min(36, fontScale.size + 20), weight: .bold, design: .rounded))
                    .foregroundStyle(.primary)
                    .lineLimit(2)
                    .minimumScaleFactor(0.65)

                Spacer(minLength: 8)

                Text(contextLabel)
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(.secondary)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 5)
                    .background(
                        Capsule(style: .continuous)
                            .fill(Color(.tertiarySystemFill))
                    )
            }

            HStack(alignment: .center, spacing: 12) {
                let ph = record?.phonetic.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
                VStack(alignment: .leading, spacing: 4) {
                    Text("音标")
                        .font(.caption2.weight(.semibold))
                        .foregroundStyle(.tertiary)
                    HStack(spacing: 8) {
                        Image(systemName: "textformat")
                            .font(.caption.weight(.semibold))
                            .foregroundStyle(.tertiary)
                        Text(ph.isEmpty ? "—" : ph)
                            .font(.system(size: fontScale.size + 1, design: .rounded))
                            .foregroundStyle(ph.isEmpty ? .tertiary : .secondary)
                    }
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 10)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color(.secondarySystemGroupedBackground))
                )

                VStack(spacing: 4) {
                    Text("读音")
                        .font(.caption2.weight(.semibold))
                        .foregroundStyle(.secondary)
                    Button {
                        speaker.speak(word.lowercased())
                    } label: {
                        Image(systemName: "speaker.wave.2.fill")
                            .font(.title2)
                            .foregroundStyle(Color.accentColor)
                            .frame(width: 44, height: 44)
                    }
                    .buttonStyle(.plain)
                    .accessibilityLabel("朗读单词")
                }
            }

            if let badge = typeBadge {
                HStack(spacing: 6) {
                    Image(systemName: "tag.fill")
                        .font(.caption2)
                    Text(badge)
                        .font(.caption.weight(.medium))
                }
                .foregroundStyle(.orange)
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(
                    Capsule(style: .continuous)
                        .fill(Color.orange.opacity(0.12))
                )
            }
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(cardBackground(elevated: true))
    }

    @ViewBuilder
    private var definitionsCard: some View {
        if let defs = record?.definitions, !defs.isEmpty {
            VStack(alignment: .leading, spacing: 14) {
                sectionTitle("释义", systemImage: "book.closed.fill")

                VStack(alignment: .leading, spacing: 12) {
                    ForEach(Array(defs.enumerated()), id: \.offset) { idx, d in
                        HStack(alignment: .top, spacing: 12) {
                            if !d.partOfSpeech.trimmingCharacters(in: .whitespaces).isEmpty {
                                Text(d.partOfSpeech)
                                    .font(.caption.weight(.semibold))
                                    .foregroundStyle(.orange)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 4)
                                    .background(
                                        RoundedRectangle(cornerRadius: 6, style: .continuous)
                                            .fill(Color.orange.opacity(0.14))
                                    )
                                    .fixedSize(horizontal: true, vertical: false)
                            }
                            Text(d.meaning)
                                .font(.system(size: fontScale.size + 1))
                                .foregroundStyle(.primary)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        if idx < defs.count - 1 {
                            Divider()
                                .opacity(0.35)
                        }
                    }
                }
            }
            .padding(18)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(cardBackground(elevated: false))
        } else {
            VStack(alignment: .leading, spacing: 10) {
                sectionTitle("释义", systemImage: "book.closed.fill")
                Text("当前内置词库中暂无该词详细释义，可加入生词本以便复习。")
                    .font(.system(size: fontScale.size))
                    .foregroundStyle(.secondary)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .padding(18)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(cardBackground(elevated: false))
        }
    }

    private var exampleCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            sectionTitle("例句", systemImage: "quote.opening")

            if loadingEx {
                HStack(spacing: 10) {
                    ProgressView()
                    Text("正在获取例句…")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
            } else {
                VStack(alignment: .leading, spacing: 10) {
                    if exampleEn.isEmpty {
                        Text("暂无")
                            .font(.system(size: fontScale.size))
                            .foregroundStyle(.tertiary)
                    } else {
                        Text(exampleEn)
                            .font(.system(size: fontScale.size + 1))
                            .italic()
                            .foregroundStyle(.primary)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    if !exampleZh.isEmpty {
                        Text(exampleZh)
                            .font(.system(size: fontScale.size))
                            .foregroundStyle(.secondary)
                            .padding(.top, 4)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(14)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color(.secondarySystemGroupedBackground))
                )
            }
        }
        .padding(18)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(cardBackground(elevated: false))
    }

    private var notebookSection: some View {
        let n = notebook.count(for: word)
        return Button {
            notebook.add(word)
        } label: {
            HStack(spacing: 10) {
                Image(systemName: "plus.circle.fill")
                    .font(.title3)
                Text(n == 0 ? "加入生词本" : "已记 \(n) 次 · 再记一次")
                    .font(.headline)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
        }
        .buttonStyle(.plain)
        .foregroundStyle(Color.white)
        .background(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .fill(Color.accentColor)
        )
    }

    // MARK: - Chrome

    private func sectionTitle(_ title: String, systemImage: String) -> some View {
        HStack(spacing: 8) {
            Image(systemName: systemImage)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(.secondary)
            Text(title)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(.secondary)
        }
    }

    private func cardBackground(elevated: Bool) -> some View {
        RoundedRectangle(cornerRadius: 18, style: .continuous)
            .fill(elevated ? Color(.secondarySystemGroupedBackground) : Color(.systemBackground))
            .shadow(color: .black.opacity(elevated ? 0.06 : 0.04), radius: elevated ? 12 : 6, x: 0, y: elevated ? 4 : 2)
    }

    @MainActor
    private func loadExample() async {
        let w = word.lowercased()
        if let ex = record?.example?.trimmingCharacters(in: .whitespacesAndNewlines), !ex.isEmpty {
            exampleEn = ex
            exampleZh = record?.exampleZh?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
            if exampleZh.isEmpty {
                exampleZh = (await DictionaryService.translateToZh(ex)) ?? ""
            }
            return
        }
        loadingEx = true
        defer { loadingEx = false }
        if let ex = await DictionaryService.fetchExample(for: w) {
            exampleEn = ex
            exampleZh = (await DictionaryService.translateToZh(ex)) ?? ""
        } else {
            exampleEn = "暂无例句（可检查网络后重试）"
        }
    }
}

// MARK: - 合并主词库 + 阅读高频 + 688（生词本详情可显示音标/释义）

private enum WordDetailMerged {
    private static var hfByWord: [String: HFRow] = [:]
    private static var mcByWord: [String: MCRow] = [:]
    private static var loaded = false
    private static let lock = NSLock()

    private struct HFRow: Decodable {
        let word: String
        let phonetic: String
        let meaning: String
    }

    private struct HFFile: Decodable {
        let entries: [HFRow]
    }

    private struct MCRow: Decodable {
        let word: String
        let meaning: String
    }

    private struct MCFile: Decodable {
        let entries: [MCRow]
    }

    private static func loadIfNeeded() {
        lock.lock()
        defer { lock.unlock() }
        if loaded { return }
        loaded = true
        if let url = Bundle.main.url(forResource: "reading_high_freq", withExtension: "json"),
           let data = try? Data(contentsOf: url),
           let file = try? JSONDecoder().decode(HFFile.self, from: data) {
            for e in file.entries {
                hfByWord[e.word.lowercased()] = e
            }
        }
        if let url = Bundle.main.url(forResource: "mc688_21day", withExtension: "json"),
           let data = try? Data(contentsOf: url),
           let file = try? JSONDecoder().decode(MCFile.self, from: data) {
            for e in file.entries {
                mcByWord[e.word.lowercased()] = e
            }
        }
    }

    /// - Parameter base: 主词库记录，在调用方（MainActor）上通过 `vocabulary.record(for:)` 取得。
    static func mergedRecord(base: WordRecord?, word: String) -> WordRecord? {
        loadIfNeeded()
        let key = word.lowercased()
        let hf = hfByWord[key]
        let mc = mcByWord[key]

        if let b = base {
            let phonetic = b.phonetic.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
                ? (hf?.phonetic ?? "").trimmingCharacters(in: .whitespacesAndNewlines)
                : b.phonetic
            let defs: [DefinitionItem]
            if !b.definitions.isEmpty {
                defs = b.definitions
            } else if let h = hf {
                defs = [DefinitionItem(partOfSpeech: "", meaning: h.meaning)]
            } else if let m = mc {
                defs = [DefinitionItem(partOfSpeech: "", meaning: m.meaning)]
            } else {
                defs = []
            }
            return WordRecord(
                word: b.word,
                phonetic: phonetic,
                type: b.type,
                definitions: defs,
                example: b.example,
                exampleZh: b.exampleZh
            )
        }
        if let h = hf {
            return WordRecord(
                word: h.word,
                phonetic: h.phonetic,
                type: "",
                definitions: [DefinitionItem(partOfSpeech: "", meaning: h.meaning)],
                example: nil,
                exampleZh: nil
            )
        }
        if let m = mc {
            return WordRecord(
                word: m.word,
                phonetic: "",
                type: "",
                definitions: [DefinitionItem(partOfSpeech: "", meaning: m.meaning)],
                example: nil,
                exampleZh: nil
            )
        }
        return nil
    }
}

private final class WordDetailSpeaker: ObservableObject {
    private let synth = AVSpeechSynthesizer()

    func speak(_ word: String) {
        let session = AVAudioSession.sharedInstance()
        try? session.setCategory(.playback, mode: .spokenAudio, options: [.duckOthers])
        try? session.setActive(true)
        let u = AVSpeechUtterance(string: word)
        u.voice = AVSpeechSynthesisVoice(language: "en-US")
        synth.speak(u)
    }
}
