import PhotosUI
import SwiftUI
import UIKit

struct ScanTabView: View {
    @EnvironmentObject private var vocabulary: VocabularyStore
    @EnvironmentObject private var notebook: NotebookStore
    @Environment(\.fontScale) private var fontScale

    @State private var selectedImage: UIImage?
    @State private var photoItem: PhotosPickerItem?
    @State private var showCamera = false
    @State private var ocrText = ""
    @State private var isOcrLoading = false
    @State private var isAiLoading = false
    @State private var errorMessage: String?
    @State private var analysis: ReadingAnalysis?
    @State private var detailWord: String?

    private var unknownWords: [String] {
        let t = ocrText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !t.isEmpty else { return [] }
        return ScanTextAnalyzer.unknownWords(in: t, vocabularyKeys: vocabulary.vocabularyKeys)
    }

    private var accuracy: Int {
        ScanTextAnalyzer.accuracyPercent(text: ocrText, unknownCount: unknownWords.count)
    }

    private var hasOcrResult: Bool {
        !ocrText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 14) {
                    imageCard
                    actionCard
                    if let err = errorMessage {
                        errorBanner(err)
                    }
                    if hasOcrResult || isOcrLoading {
                        recognizedTextCard
                    }
                    if hasOcrResult {
                        vocabularyInsightCard
                        analysisCard
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
            }
            .background(Color(.systemGroupedBackground))
            .navigationTitle("扫描识图")
            .onChange(of: photoItem) { new in
                guard let new else { return }
                Task {
                    if let data = try? await new.loadTransferable(type: Data.self),
                       let img = UIImage(data: data) {
                        await MainActor.run {
                            selectedImage = img
                            ocrText = ""
                            analysis = nil
                            errorMessage = nil
                        }
                    }
                }
            }
            .sheet(isPresented: $showCamera) {
                CameraPicker(image: $selectedImage)
                    .ignoresSafeArea()
            }
            .sheet(item: Binding(
                get: { detailWord.map { ScanSheetWord(id: $0) } },
                set: { detailWord = $0?.id }
            )) { item in
                WordDetailSheet(word: item.id, context: .result)
                    .environmentObject(vocabulary)
                    .environmentObject(notebook)
                    .environment(\.fontScale, fontScale)
                    .presentationDetents([.medium, .large])
                    .presentationDragIndicator(.visible)
            }
        }
    }

    // MARK: - Image

    private var imageCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            ScanSectionTitle(icon: "doc.viewfinder", title: "图片", subtitle: "选择教材、试卷等含英文的照片")

            ZStack {
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .fill(Color(.secondarySystemGroupedBackground))

                if let img = selectedImage {
                    Image(uiImage: img)
                        .resizable()
                        .scaledToFit()
                        .frame(maxHeight: 240)
                        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                        .padding(10)
                } else {
                    VStack(spacing: 12) {
                        Image(systemName: "photo.badge.plus")
                            .font(.system(size: 40))
                            .symbolRenderingMode(.hierarchical)
                            .foregroundStyle(.tertiary)
                        Text("点击下方选择相册或拍照")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 36)
                }
            }
            .overlay {
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .strokeBorder(Color(.separator).opacity(0.35), lineWidth: 0.5)
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(scanCardBackground(elevated: true))
    }

    // MARK: - Actions

    private var actionCard: some View {
        VStack(alignment: .leading, spacing: 14) {
            ScanSectionTitle(icon: "wand.and.stars", title: "操作", subtitle: nil)

            HStack(spacing: 10) {
                PhotosPicker(selection: $photoItem, matching: .images) {
                    Label("相册", systemImage: "photo.on.rectangle.angled")
                        .font(.subheadline.weight(.medium))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.bordered)

                if UIImagePickerController.isSourceTypeAvailable(.camera) {
                    Button {
                        showCamera = true
                    } label: {
                        Label("拍照", systemImage: "camera.fill")
                            .font(.subheadline.weight(.medium))
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                    }
                    .buttonStyle(.bordered)
                }

                Button {
                    selectedImage = nil
                    photoItem = nil
                    ocrText = ""
                    analysis = nil
                    errorMessage = nil
                } label: {
                    Image(systemName: "trash")
                        .font(.body.weight(.medium))
                        .frame(width: 44, height: 44)
                }
                .buttonStyle(.bordered)
                .tint(.secondary)
                .accessibilityLabel("清除图片与结果")
            }

            Button {
                Task { await runOcr() }
            } label: {
                HStack(spacing: 10) {
                    if isOcrLoading {
                        ProgressView()
                            .tint(.white)
                        Text("正在识别英文…")
                            .font(.headline)
                    } else {
                        Image(systemName: "text.viewfinder")
                            .font(.headline)
                        Text("识别图中英文")
                            .font(.headline)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 15)
            }
            .buttonStyle(.plain)
            .foregroundStyle(Color.white)
            .background(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .fill(selectedImage == nil || isOcrLoading ? Color.accentColor.opacity(0.45) : Color.accentColor)
            )
            .disabled(selectedImage == nil || isOcrLoading)

            HStack(alignment: .top, spacing: 8) {
                Image(systemName: "network")
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(.tertiary)
                Text("OCR 与 AI 分析需本机 8787 端口代理（与网页版一致）。")
                    .font(.caption)
                    .foregroundStyle(.tertiary)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .padding(10)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .fill(Color(.tertiarySystemFill))
            )
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(scanCardBackground(elevated: false))
    }

    // MARK: - Error

    private func errorBanner(_ message: String) -> some View {
        HStack(alignment: .top, spacing: 10) {
            Image(systemName: "exclamationmark.triangle.fill")
                .foregroundStyle(.orange)
            Text(message)
                .font(.subheadline)
                .foregroundStyle(.primary)
                .fixedSize(horizontal: false, vertical: true)
            Spacer(minLength: 0)
        }
        .padding(14)
        .background(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .fill(Color.orange.opacity(0.12))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .strokeBorder(Color.orange.opacity(0.25), lineWidth: 0.5)
        )
    }

    // MARK: - Recognized text

    private var recognizedTextCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                ScanSectionTitle(icon: "text.alignleft", title: "识别结果", subtitle: nil)
                Spacer()
                if isOcrLoading {
                    ProgressView()
                        .scaleEffect(0.9)
                }
            }

            if isOcrLoading && ocrText.isEmpty {
                Text("正在从图片中提取英文…")
                    .font(.system(size: fontScale.size))
                    .foregroundStyle(.secondary)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(14)
                    .background(
                        RoundedRectangle(cornerRadius: 12, style: .continuous)
                            .fill(Color(.secondarySystemGroupedBackground))
                    )
            } else {
                Text(ocrText.isEmpty ? "（暂无文本）" : ocrText)
                    .font(.system(size: fontScale.size + 1))
                    .lineSpacing(5)
                    .foregroundStyle(ocrText.isEmpty ? .tertiary : .primary)
                    .textSelection(.enabled)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(14)
                    .background(
                        RoundedRectangle(cornerRadius: 12, style: .continuous)
                            .fill(Color(.secondarySystemGroupedBackground))
                    )
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(scanCardBackground(elevated: false))
    }

    // MARK: - Stats & words

    private var vocabularyInsightCard: some View {
        VStack(alignment: .leading, spacing: 16) {
            ScanSectionTitle(icon: "chart.bar.doc.horizontal", title: "词汇与难度", subtitle: "相对词库的启发式估计")

            VStack(alignment: .leading, spacing: 8) {
                HStack(alignment: .firstTextBaseline) {
                    Text("大致可读性")
                        .font(.subheadline.weight(.medium))
                        .foregroundStyle(.secondary)
                    Spacer()
                    Text("\(accuracy)%")
                        .font(.system(size: 28, weight: .bold, design: .rounded))
                        .foregroundStyle(.teal)
                }
                ProgressView(value: Double(min(accuracy, 100)), total: 100)
                    .tint(.teal)
                    .scaleEffect(x: 1, y: 1.35, anchor: .center)
            }
            .padding(14)
            .background(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(Color.teal.opacity(0.08))
            )

            if unknownWords.isEmpty {
                Label("未发现明显超纲词", systemImage: "checkmark.circle.fill")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            } else {
                VStack(alignment: .leading, spacing: 10) {
                    Text("可能需关注（\(unknownWords.count)）")
                        .font(.caption.weight(.semibold))
                        .foregroundStyle(.secondary)
                        .textCase(.uppercase)
                        .tracking(0.3)

                    FlowWordChips(words: unknownWords, fontScale: fontScale.size) { w in
                        detailWord = w
                    }
                }
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(scanCardBackground(elevated: false))
    }

    // MARK: - Analysis

    private var analysisCard: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack(alignment: .center) {
                ScanSectionTitle(icon: "book.pages", title: "阅读分析", subtitle: nil)
                Spacer(minLength: 8)
                Button {
                    Task { await runAiAnalysis() }
                } label: {
                    HStack(spacing: 6) {
                        if isAiLoading {
                            ProgressView()
                                .scaleEffect(0.85)
                        } else {
                            Image(systemName: "sparkles")
                        }
                        Text(isAiLoading ? "分析中" : "AI 深度")
                            .font(.caption.weight(.semibold))
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.small)
                .disabled(isAiLoading || ocrText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
            }

            if let a = analysis {
                ReadingAnalysisContent(analysis: a, fontSize: fontScale.size)
            } else if let h = ReadingAnalysisEngine.analyzeHeuristic(text: ocrText) {
                ReadingAnalysisContent(analysis: h, fontSize: fontScale.size)
            } else {
                Text("文本过短，暂无分析。")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(scanCardBackground(elevated: false))
    }

    // MARK: - Chrome

    private func scanCardBackground(elevated: Bool) -> some View {
        RoundedRectangle(cornerRadius: 18, style: .continuous)
            .fill(Color(.systemBackground))
            .shadow(color: .black.opacity(elevated ? 0.07 : 0.04), radius: elevated ? 10 : 6, x: 0, y: elevated ? 3 : 2)
    }

    @MainActor
    private func runOcr() async {
        guard let img = selectedImage else { return }
        errorMessage = nil
        isOcrLoading = true
        analysis = nil
        defer { isOcrLoading = false }
        do {
            let text = try await AIService.transcribeImage(img, ocrModel: AIService.defaultOcrModel)
            ocrText = text
            analysis = ReadingAnalysisEngine.analyzeHeuristic(text: text)
        } catch {
            errorMessage = error.localizedDescription
            ocrText = ""
        }
    }

    @MainActor
    private func runAiAnalysis() async {
        let t = ocrText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !t.isEmpty else { return }
        errorMessage = nil
        isAiLoading = true
        defer { isAiLoading = false }
        do {
            let raw = try await AIService.analyzeReading(text: t, readModel: AIService.defaultReadModel)
            if let parsed = ReadingAnalysisEngine.parseAIResponse(raw) {
                analysis = parsed
            } else {
                errorMessage = "AI 返回无法解析为 JSON，已保留启发式结果。"
                analysis = ReadingAnalysisEngine.analyzeHeuristic(text: t)
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

// MARK: - Section title

private struct ScanSectionTitle: View {
    let icon: String
    let title: String
    var subtitle: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 8) {
                Image(systemName: icon)
                    .font(.body.weight(.semibold))
                    .foregroundStyle(.secondary)
                Text(title)
                    .font(.headline)
            }
            if let subtitle, !subtitle.isEmpty {
                Text(subtitle)
                    .font(.caption)
                    .foregroundStyle(.tertiary)
            }
        }
    }
}

private struct ScanSheetWord: Identifiable {
    let id: String
}

// MARK: - Camera

private struct CameraPicker: UIViewControllerRepresentable {
    @Binding var image: UIImage?
    @Environment(\.dismiss) private var dismiss

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let p = UIImagePickerController()
        p.sourceType = .camera
        p.delegate = context.coordinator
        return p
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    final class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let parent: CameraPicker
        init(_ parent: CameraPicker) { self.parent = parent }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parent.dismiss()
        }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            parent.image = info[.originalImage] as? UIImage
            parent.dismiss()
        }
    }
}

// MARK: - Word chips

private struct FlowWordChips: View {
    let words: [String]
    var fontScale: CGFloat
    var onTap: (String) -> Void

    var body: some View {
        LazyVGrid(
            columns: [GridItem(.adaptive(minimum: 76), spacing: 8, alignment: .leading)],
            alignment: .leading,
            spacing: 8
        ) {
            ForEach(words, id: \.self) { w in
                Button {
                    onTap(w)
                } label: {
                    Text(w)
                        .font(.system(size: max(12, fontScale * 0.85), weight: .medium))
                        .lineLimit(1)
                        .minimumScaleFactor(0.8)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 9)
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.plain)
                .foregroundStyle(Color.orange)
                .background(
                    Capsule(style: .continuous)
                        .fill(Color.orange.opacity(0.14))
                )
                .overlay(
                    Capsule(style: .continuous)
                        .strokeBorder(Color.orange.opacity(0.22), lineWidth: 0.5)
                )
            }
        }
    }
}

// MARK: - Analysis UI

private struct ReadingAnalysisContent: View {
    let analysis: ReadingAnalysis
    var fontSize: CGFloat

    private var modeLabel: String {
        analysis.mode == "ai" ? "AI 生成" : "本地启发式"
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            Text(modeLabel)
                .font(.caption.weight(.semibold))
                .foregroundStyle(.secondary)
                .padding(.horizontal, 10)
                .padding(.vertical, 5)
                .background(
                    Capsule(style: .continuous)
                        .fill(Color(.tertiarySystemFill))
                )

            if !analysis.coreViewpointZh.isEmpty {
                analysisBlock(
                    icon: "lightbulb.fill",
                    title: "核心观点",
                    analysis.coreViewpointZh,
                    titleColor: .yellow
                )
            }
            if !analysis.paragraphGists.isEmpty {
                VStack(alignment: .leading, spacing: 10) {
                    Label("段落大意", systemImage: "list.bullet.rectangle")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.secondary)
                    ForEach(0..<analysis.paragraphGists.count, id: \.self) { i in
                        let g = analysis.paragraphGists[i]
                        VStack(alignment: .leading, spacing: 8) {
                            Text("第 \(g.index) 段")
                                .font(.caption.weight(.bold))
                                .foregroundStyle(Color.accentColor)
                            Text(g.gistZh)
                                .font(.system(size: fontSize * 0.95))
                                .fixedSize(horizontal: false, vertical: true)
                            if !g.keySentenceEn.isEmpty {
                                Text(g.keySentenceEn)
                                    .font(.system(size: fontSize * 0.82))
                                    .foregroundStyle(.secondary)
                                    .italic()
                                    .padding(10)
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                    .background(
                                        RoundedRectangle(cornerRadius: 8, style: .continuous)
                                            .fill(Color(.secondarySystemGroupedBackground))
                                    )
                            }
                        }
                        .padding(12)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(
                            RoundedRectangle(cornerRadius: 12, style: .continuous)
                                .fill(Color(.secondarySystemGroupedBackground).opacity(0.65))
                        )
                    }
                }
            }
            if !analysis.examPoints.isEmpty {
                bulletBlock(icon: "pencil.and.list.clipboard", title: "考点提示", items: analysis.examPoints, dot: "•")
            }
            if !analysis.logicRelations.isEmpty {
                bulletBlock(icon: "arrow.triangle.branch", title: "逻辑关系", items: analysis.logicRelations, dot: "→")
            }
            if !analysis.howToSolveZh.isEmpty {
                analysisBlock(
                    icon: "checklist",
                    title: "解题策略",
                    analysis.howToSolveZh,
                    titleColor: .blue
                )
            }
        }
    }

    private func analysisBlock(icon: String, title: String, _ body: String, titleColor: Color) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Label(title, systemImage: icon)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(titleColor)
            Text(body)
                .font(.system(size: fontSize * 0.95))
                .fixedSize(horizontal: false, vertical: true)
        }
        .padding(12)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(Color(.secondarySystemGroupedBackground))
        )
    }

    private func bulletBlock(icon: String, title: String, items: [String], dot: String) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Label(title, systemImage: icon)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(.secondary)
            VStack(alignment: .leading, spacing: 6) {
                ForEach(Array(items.enumerated()), id: \.offset) { _, s in
                    HStack(alignment: .top, spacing: 8) {
                        Text(dot)
                            .foregroundStyle(.tertiary)
                            .font(.caption.weight(.bold))
                        Text(s)
                            .font(.system(size: fontSize * 0.9))
                            .fixedSize(horizontal: false, vertical: true)
                    }
                }
            }
        }
        .padding(12)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(Color(.secondarySystemGroupedBackground))
        )
    }
}
