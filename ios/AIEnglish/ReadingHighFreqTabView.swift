import AVFoundation
import SwiftUI

private struct ReadingHighFreqFile: Codable {
    let version: Int?
    let label: String?
    let note: String?
    let entries: [ReadingHighFreqEntry]
}

private struct ReadingHighFreqEntry: Codable, Identifiable {
    var id: Int { rank }
    let rank: Int
    let word: String
    let phonetic: String
    let meaning: String
    let frequency: Int
}

/// 中考阅读高频词表 + 系统英语朗读。
struct ReadingHighFreqTabView: View {
    @EnvironmentObject private var notebook: NotebookStore
    @Environment(\.fontScale) private var fontScale
    @State private var file: ReadingHighFreqFile?
    @State private var loadError: String?
    @StateObject private var speaker = ReadingWordSpeaker()

    var body: some View {
        Group {
            if let err = loadError, file == nil {
                Text(err)
                    .foregroundStyle(.secondary)
                    .padding()
            } else if file == nil {
                ProgressView("加载中…")
            } else if let f = file {
                List {
                    if let note = f.note, !note.isEmpty {
                        Section {
                            Text(note)
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                    }
                    Section {
                        Text("点击单词或 🔊 朗读（系统英语，离线可用）。")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                    }
                    ForEach(f.entries) { e in
                        HStack(alignment: .top, spacing: 10) {
                            Text("\(e.rank)")
                                .font(.caption.weight(.medium))
                                .foregroundStyle(.secondary)
                                .frame(width: 32, alignment: .leading)
                            VStack(alignment: .leading, spacing: 4) {
                                HStack(spacing: 8) {
                                    Button {
                                        speaker.speak(e.word)
                                    } label: {
                                        Text(e.word)
                                            .font(.system(size: fontScale.size + 1, weight: .semibold))
                                            .foregroundStyle(.primary)
                                    }
                                    .buttonStyle(.plain)
                                    Button {
                                        speaker.speak(e.word)
                                    } label: {
                                        Image(systemName: "speaker.wave.2.fill")
                                            .font(.body)
                                            .foregroundStyle(Color.accentColor)
                                    }
                                    .buttonStyle(.borderless)
                                    .accessibilityLabel("朗读 \(e.word)")
                                    Button {
                                        notebook.add(e.word)
                                    } label: {
                                        Text("+")
                                            .font(.system(size: 22, weight: .bold))
                                            .foregroundStyle(Color.accentColor)
                                            .frame(minWidth: 36, minHeight: 36)
                                    }
                                    .buttonStyle(.plain)
                                    .accessibilityLabel("加入生词本 \(e.word)")
                                }
                                Text(e.phonetic)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                                Text(e.meaning)
                                    .font(.system(size: fontScale.size))
                                    .foregroundStyle(.primary)
                            }
                            Spacer(minLength: 0)
                            Text("\(e.frequency)")
                                .font(.caption.weight(.semibold))
                                .foregroundStyle(Color.accentColor)
                                .frame(width: 28, alignment: .trailing)
                        }
                        .padding(.vertical, 4)
                    }
                }
                .listStyle(.insetGrouped)
            }
        }
        .task {
            await load()
        }
    }

    private func load() async {
        guard let url = Bundle.main.url(forResource: "reading_high_freq", withExtension: "json") else {
            loadError = "缺少 reading_high_freq.json"
            return
        }
        do {
            let data = try Data(contentsOf: url)
            let decoded = try JSONDecoder().decode(ReadingHighFreqFile.self, from: data)
            file = decoded
        } catch {
            loadError = error.localizedDescription
        }
    }
}

private final class ReadingWordSpeaker: ObservableObject {
    private let synth = AVSpeechSynthesizer()

    func speak(_ word: String) {
        let session = AVAudioSession.sharedInstance()
        try? session.setCategory(.playback, mode: .spokenAudio, options: [.duckOthers])
        try? session.setActive(true)
        synth.stopSpeaking(at: .immediate)
        let u = AVSpeechUtterance(string: word)
        u.voice = AVSpeechSynthesisVoice(language: "en-US")
        synth.speak(u)
    }
}
