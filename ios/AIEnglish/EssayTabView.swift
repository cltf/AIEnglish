import SwiftUI
import AVFoundation

struct EssayTabView: View {
    /// 非空时固定为「语文」或「英语」作文列表，不显示科目切换。
    var fixedSubject: String? = nil

    @Environment(\.fontScale) private var fontScale
    @State private var exams: [EssayExam] = []
    @State private var loadError: String?
    @State private var subjectKey: String = "english"
    @StateObject private var ttsPlayer = EssayTTSPlayer()

    private var effectiveSubject: String {
        fixedSubject ?? subjectKey
    }

    private var filteredExams: [EssayExam] {
        exams.filter { $0.subject == effectiveSubject }
    }

    private var navigationTitleText: String {
        if fixedSubject == "chinese" { return "语文作文" }
        if fixedSubject == "english" { return "英语作文" }
        return "作文"
    }

    var body: some View {
        NavigationStack {
            Group {
                if exams.isEmpty, loadError == nil {
                    ProgressView("加载中…")
                } else if let err = loadError {
                    Text(err)
                        .foregroundStyle(.secondary)
                        .padding()
                } else if exams.isEmpty {
                    VStack(spacing: 12) {
                        Image(systemName: "doc.text")
                            .font(.largeTitle)
                            .foregroundStyle(.secondary)
                        Text("暂无作文数据")
                            .font(.headline)
                        Text("请确认 essays.json 已加入应用包。")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                } else {
                    VStack(spacing: 0) {
                        if fixedSubject == nil {
                            HStack {
                                Text("科目")
                                    .font(.subheadline)
                                    .foregroundStyle(.secondary)
                                Picker("科目", selection: $subjectKey) {
                                    Text("英语").tag("english")
                                    Text("语文").tag("chinese")
                                }
                                .pickerStyle(.menu)
                                Spacer()
                            }
                            .padding(.horizontal, 16)
                            .padding(.vertical, 10)
                            .background(Color(.secondarySystemGroupedBackground))
                        }

                        if filteredExams.isEmpty {
                            VStack(spacing: 10) {
                                Image(systemName: "tray")
                                    .font(.largeTitle)
                                    .foregroundStyle(.secondary)
                                Text("该科目暂无范文")
                                    .font(.headline)
                                Text("请切换科目或稍后再试")
                                    .font(.subheadline)
                                    .foregroundStyle(.secondary)
                            }
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                            .padding()
                        } else {
                            List {
                                ForEach(filteredExams) { exam in
                                    Section {
                                        ForEach(exam.samples) { sample in
                                            NavigationLink {
                                                EssayDetailView(exam: exam, sample: sample, ttsPlayer: ttsPlayer)
                                            } label: {
                                                Text(sample.title)
                                                    .font(.system(size: fontScale.size))
                                                    .lineLimit(2)
                                                    .padding(.vertical, 2)
                                            }
                                        }
                                    } header: {
                                        Text("📚 \(exam.title)")
                                    }
                                }
                            }
                            .listStyle(.insetGrouped)
                        }
                    }
                }
            }
            .navigationTitle(navigationTitleText)
            .task {
                if let f = fixedSubject {
                    subjectKey = f
                }
                do {
                    exams = try EssayDataLoader.load()
                } catch {
                    loadError = error.localizedDescription
                }
            }
        }
    }
}

private let essayPlaybackRates: [Float] = [0.75, 1, 1.25, 1.5, 2]

struct EssayTTSControls: View {
    let sampleId: String
    @ObservedObject var ttsPlayer: EssayTTSPlayer

    var body: some View {
        HStack(spacing: 12) {
            Button {
                Task { await ttsPlayer.toggle(sampleId: sampleId) }
            } label: {
                HStack(spacing: 8) {
                    Image(systemName: ttsPlayer.isPlaying(sampleId: sampleId) ? "pause.fill" : "play.fill")
                        .font(.system(size: 15, weight: .semibold))
                    Text(buttonText)
                        .font(.subheadline.weight(.semibold))
                }
                .foregroundStyle(.white)
                .padding(.horizontal, 20)
                .padding(.vertical, 12)
                .background(Color.accentColor, in: Capsule())
            }
            .buttonStyle(.plain)
            .disabled(ttsPlayer.isLoading(sampleId: sampleId))
            .opacity(ttsPlayer.isLoading(sampleId: sampleId) ? 0.55 : 1)

            Slider(
                value: Binding(
                    get: { Double(ttsPlayer.progress(sampleId: sampleId)) },
                    set: { ttsPlayer.seek(sampleId: sampleId, fraction: Float($0)) }
                ),
                in: 0...1,
                onEditingChanged: { editing in
                    ttsPlayer.setScrubbing(editing)
                }
            )
            .tint(Color.accentColor)
            .frame(maxWidth: .infinity)

            Text(timeText)
                .font(.caption)
                .foregroundStyle(.secondary)
                .monospacedDigit()
                .frame(minWidth: 92, alignment: .trailing)

            Menu {
                ForEach(essayPlaybackRates, id: \.self) { r in
                    Button {
                        ttsPlayer.setPlaybackRate(r)
                    } label: {
                        HStack {
                            Text(speedLabel(r))
                            if abs(r - ttsPlayer.playbackRate) < 0.001 {
                                Image(systemName: "checkmark")
                            }
                        }
                    }
                }
            } label: {
                HStack(spacing: 4) {
                    Text(speedLabel(ttsPlayer.playbackRate))
                    Image(systemName: "chevron.down")
                        .font(.caption2.weight(.semibold))
                }
                .font(.caption.weight(.medium))
                .foregroundStyle(.primary)
                .padding(.horizontal, 12)
                .padding(.vertical, 10)
                .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 12, style: .continuous))
            }
        }
    }

    private func speedLabel(_ r: Float) -> String {
        if r == 1 { return "1×" }
        let s = String(format: "%g", Double(r))
        return "\(s)×"
    }

    private var progressValue: Double {
        let p = ttsPlayer.progress(sampleId: sampleId)
        return min(max(Double(p), 0), 1)
    }

    private var timeText: String {
        "\(ttsPlayer.currentTimeText(sampleId: sampleId)) / \(ttsPlayer.durationText(sampleId: sampleId))"
    }

    private var buttonText: String {
        if ttsPlayer.isLoading(sampleId: sampleId) { return "生成中…" }
        if ttsPlayer.isPlaying(sampleId: sampleId) { return "暂停" }
        if ttsPlayer.hasAudio(sampleId: sampleId) { return "继续播放" }
        return "播放"
    }
}

@MainActor
final class EssayTTSPlayer: NSObject, ObservableObject, AVAudioPlayerDelegate {
    @Published private(set) var currentSampleId: String?
    @Published private(set) var isBusy = false
    @Published private(set) var isNowPlaying = false
    @Published private(set) var current: TimeInterval = 0
    @Published private(set) var duration: TimeInterval = 0
    /// 离线音频缺失或播放失败时展示。
    @Published private(set) var playbackError: String?
    @Published private(set) var playbackRate: Float = 1

    private var isScrubbing = false
    private var player: AVAudioPlayer?
    private var cachedDataBySampleId: [String: Data] = [:]
    private var timer: Timer?
    private var loadingSampleId: String?

    deinit {
        timer?.invalidate()
    }

    func toggle(sampleId: String) async {
        if let loading = loadingSampleId, loading == sampleId { return }
        if currentSampleId == sampleId, let p = player {
            if p.isPlaying {
                p.pause()
                isNowPlaying = false
            } else {
                p.rate = playbackRate
                p.play()
                isNowPlaying = true
            }
            return
        }

        stopCurrent(reset: true)
        loadingSampleId = sampleId
        isBusy = true
        defer {
            loadingSampleId = nil
            isBusy = false
        }

        playbackError = nil
        do {
            try configurePlaybackSession()
            let audioData = try getAudio(sampleId: sampleId)
            let p = try AVAudioPlayer(data: audioData)
            p.delegate = self
            p.enableRate = true
            p.prepareToPlay()
            p.rate = playbackRate
            player = p
            currentSampleId = sampleId
            current = 0
            duration = p.duration
            isNowPlaying = p.play()
            startTimer()
        } catch {
            playbackError = error.localizedDescription
            stopCurrent(reset: true)
        }
    }

    /// 使用扬声器播放 TTS，不受静音拨片影响（与媒体类应用一致）。
    private func configurePlaybackSession() throws {
        let session = AVAudioSession.sharedInstance()
        try session.setCategory(.playback, mode: .default, options: [.mixWithOthers])
        try session.setActive(true)
    }

    func setPlaybackRate(_ rate: Float) {
        let clamped = min(max(rate, 0.5), 2)
        playbackRate = clamped
        player?.rate = clamped
    }

    func setScrubbing(_ scrubbing: Bool) {
        isScrubbing = scrubbing
    }

    func seek(sampleId: String, fraction: Float) {
        guard currentSampleId == sampleId, let p = player, duration > 0 else { return }
        let f = min(max(fraction, 0), 1)
        let t = TimeInterval(f) * duration
        p.currentTime = t
        current = t
    }

    func isLoading(sampleId: String) -> Bool { loadingSampleId == sampleId }
    func isPlaying(sampleId: String) -> Bool { currentSampleId == sampleId && isNowPlaying }
    func hasAudio(sampleId: String) -> Bool { cachedDataBySampleId[sampleId] != nil || currentSampleId == sampleId }
    func progress(sampleId: String) -> Float {
        guard currentSampleId == sampleId, duration > 0 else { return 0 }
        return Float(current / duration)
    }
    func currentTimeText(sampleId: String) -> String {
        guard currentSampleId == sampleId else { return "0:00" }
        return Self.formatTime(current)
    }
    func durationText(sampleId: String) -> String {
        guard currentSampleId == sampleId else { return "0:00" }
        return Self.formatTime(duration)
    }

    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        isNowPlaying = false
        current = duration
        timer?.invalidate()
        timer = nil
    }

    private func getAudio(sampleId: String) throws -> Data {
        if let d = cachedDataBySampleId[sampleId] { return d }
        let candidates = [
            Bundle.main.url(forResource: sampleId, withExtension: "wav", subdirectory: "audio/essays"),
            Bundle.main.url(forResource: sampleId, withExtension: "wav", subdirectory: "essays"),
        ]
        guard let url = candidates.compactMap({ $0 }).first else {
            throw NSError(
                domain: "AIEnglish",
                code: 404,
                userInfo: [NSLocalizedDescriptionKey: "未找到离线音频：\(sampleId).wav。请先运行脚本生成，并重新编译打包。"]
            )
        }
        let d = try Data(contentsOf: url)
        cachedDataBySampleId[sampleId] = d
        return d
    }

    private func stopCurrent(reset: Bool) {
        player?.stop()
        player = nil
        isNowPlaying = false
        timer?.invalidate()
        timer = nil
        if reset {
            currentSampleId = nil
            current = 0
            duration = 0
        }
    }

    private func startTimer() {
        timer?.invalidate()
        timer = Timer.scheduledTimer(withTimeInterval: 0.25, repeats: true) { [weak self] _ in
            guard let self, let p = self.player, !self.isScrubbing else { return }
            self.current = p.currentTime
            self.duration = p.duration
            self.isNowPlaying = p.isPlaying
        }
    }

    private static func formatTime(_ sec: TimeInterval) -> String {
        let total = max(Int(sec.rounded(.down)), 0)
        return "\(total / 60):\(String(format: "%02d", total % 60))"
    }
}

#Preview {
    EssayTabView()
        .environment(\.fontScale, .standard)
}
