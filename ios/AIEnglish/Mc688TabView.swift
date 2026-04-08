import AVFoundation
import SwiftUI

private struct Mc688File: Codable {
    let version: Int?
    let label: String?
    let subtitle: String?
    let entries: [Mc688Entry]
}

private struct Mc688Entry: Codable, Identifiable, Hashable {
    var id: Int { rank }
    let rank: Int
    let day: Int
    let word: String
    let meaning: String
}

/// 中考英语单项选择核心高频 688 词 · 21 天 + 听写
struct Mc688TabView: View {
    @EnvironmentObject private var notebook: NotebookStore
    @Environment(\.fontScale) private var fontScale
    @StateObject private var speaker = Mc688WordSpeaker()
    @State private var file: Mc688File?
    @State private var loadError: String?
    @State private var selectedDay = 1
    @State private var countStr = "10"
    @State private var shuffle = false
    @State private var session: [Mc688Entry] = []
    @State private var idx = 0
    @State private var answer = ""
    @State private var feedback: String?

    var body: some View {
        Group {
            if let err = loadError, file == nil {
                Text(err).foregroundStyle(.secondary).padding()
            } else if file == nil {
                ProgressView("加载中…")
            } else if let f = file {
                content(f: f)
            }
        }
        .task { await load() }
    }

    @ViewBuilder
    private func content(f: Mc688File) -> some View {
        let pool = f.entries.filter { $0.day == selectedDay }
        let maxN = max(1, min(33, pool.count))
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                if let sub = f.subtitle, !sub.isEmpty {
                    Text(sub).font(.caption).foregroundStyle(.secondary)
                }
                Text("选择天数").font(.caption).foregroundStyle(.secondary)
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(1 ... 21, id: \.self) { d in
                            Button {
                                selectedDay = d
                                session = []
                                feedback = nil
                            } label: {
                                Text("第\(d)天")
                                    .font(.caption.weight(.medium))
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(selectedDay == d ? Color.accentColor.opacity(0.2) : Color(.secondarySystemGroupedBackground))
                                    .clipShape(RoundedRectangle(cornerRadius: 8))
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
                Divider()
                Text("听写").font(.headline)
                Text("从当天词汇中抽取若干词朗读，请输入英文。").font(.caption).foregroundStyle(.secondary)
                HStack {
                    Text("词数")
                    TextField("", text: $countStr)
                        .keyboardType(.numberPad)
                        .textFieldStyle(.roundedBorder)
                        .frame(width: 56)
                    Text("/ \(maxN)").font(.caption).foregroundStyle(.secondary)
                }
                Toggle("随机顺序", isOn: $shuffle)
                HStack {
                    Button("开始听写") {
                        startSession(pool: pool, maxN: maxN)
                    }
                    .buttonStyle(.borderedProminent)
                    Button("再听一遍") {
                        if idx < session.count {
                            speaker.speak(session[idx].word)
                        }
                    }
                    .disabled(session.isEmpty)
                }
                if !session.isEmpty, idx < session.count {
                    let cur = session[idx]
                    VStack(alignment: .leading, spacing: 8) {
                        Text("第 \(idx + 1) / \(session.count) 词 · 序号 \(cur.rank)")
                            .font(.caption.weight(.semibold))
                            .foregroundStyle(Color.accentColor)
                        TextField("输入英文单词", text: $answer)
                            .textFieldStyle(.roundedBorder)
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()
                        HStack {
                            Button("提交") {
                                let ok = answer.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() == cur.word.lowercased()
                                feedback = ok ? "正确" : "正确写法：\(cur.word)"
                            }
                            Button(idx >= session.count - 1 ? "结束" : "下一词") {
                                if idx >= session.count - 1 {
                                    session = []
                                    feedback = "本轮完成"
                                    return
                                }
                                idx += 1
                                answer = ""
                                feedback = nil
                                speaker.speak(session[idx].word)
                            }
                        }
                    }
                    if let fb = feedback {
                        Text(fb).font(.caption).foregroundStyle(.secondary)
                    }
                }
                Divider().padding(.vertical, 4)
                Text("本日词表").font(.headline)
                ForEach(pool) { e in
                    HStack(alignment: .top, spacing: 10) {
                        Text("\(e.rank)")
                            .font(.caption.weight(.medium))
                            .foregroundStyle(.secondary)
                            .frame(width: 36, alignment: .leading)
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
                                Button {
                                    notebook.add(e.word)
                                } label: {
                                    Text("+")
                                        .font(.system(size: 22, weight: .bold))
                                        .foregroundStyle(Color.accentColor)
                                        .frame(minWidth: 36, minHeight: 36)
                                }
                                .buttonStyle(.plain)
                            }
                            Text(e.meaning)
                                .font(.system(size: fontScale.size))
                                .foregroundStyle(.primary)
                        }
                    }
                    .padding(.vertical, 4)
                }
            }
            .padding()
        }
    }

    private func startSession(pool: [Mc688Entry], maxN: Int) {
        guard !pool.isEmpty else { return }
        let n = min(max(1, Int(countStr) ?? 10), maxN)
        var pick = Array(pool.prefix(n))
        if shuffle {
            pick = pool.shuffled().prefix(n).map { $0 }
        }
        session = pick
        idx = 0
        answer = ""
        feedback = nil
        speaker.speak(pick[0].word)
    }

    private func load() async {
        guard let url = Bundle.main.url(forResource: "mc688_21day", withExtension: "json") else {
            loadError = "缺少 mc688_21day.json"
            return
        }
        do {
            let data = try Data(contentsOf: url)
            file = try JSONDecoder().decode(Mc688File.self, from: data)
        } catch {
            loadError = error.localizedDescription
        }
    }
}

private final class Mc688WordSpeaker: ObservableObject {
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
