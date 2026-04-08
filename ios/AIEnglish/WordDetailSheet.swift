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
    @State private var exampleEn = ""
    @State private var exampleZh = ""
    @State private var loadingEx = false

    private var record: WordRecord? { vocabulary.record(for: word) }

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

            if let ph = record?.phonetic.trimmingCharacters(in: .whitespacesAndNewlines), !ph.isEmpty {
                HStack(spacing: 8) {
                    Image(systemName: "textformat")
                        .font(.caption.weight(.semibold))
                        .foregroundStyle(.tertiary)
                    Text(ph)
                        .font(.system(size: fontScale.size + 1, design: .rounded))
                        .foregroundStyle(.secondary)
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 10)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color(.secondarySystemGroupedBackground))
                )
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
