import SwiftUI

struct VocabularyTabView: View {
    @EnvironmentObject private var vocabulary: VocabularyStore
    @EnvironmentObject private var notebook: NotebookStore
    @Environment(\.fontScale) private var fontScale
    @State private var search = ""
    @State private var filterType = "ALL"
    @State private var page = 1
    @State private var showFilter = false
    @State private var selectedWord: String?
    private let pageSize = 20

    private var filtered: [WordRecord] {
        vocabulary.listFiltered(type: filterType, search: search)
    }

    private var totalPages: Int {
        max(1, (filtered.count + pageSize - 1) / pageSize)
    }

    private var pageItems: [WordRecord] {
        let p = min(max(1, page), totalPages)
        let start = (p - 1) * pageSize
        return Array(filtered.dropFirst(start).prefix(pageSize))
    }

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    TextField("搜索单词…", text: $search)
                        .textFieldStyle(.roundedBorder)
                        .autocorrectionDisabled()
                        .onChange(of: search) { _ in page = 1 }
                    Button("筛选") { showFilter = true }
                }
                .padding(.horizontal)

                Text(summaryText)
                    .font(.system(size: fontScale.size * 0.85))
                    .foregroundStyle(.secondary)
                    .padding(.horizontal)

                List(pageItems) { rec in
                    Button {
                        selectedWord = rec.word
                    } label: {
                        VStack(alignment: .leading, spacing: 6) {
                            Text(rec.word)
                                .font(.system(size: fontScale.size + 1, weight: .semibold))
                            Text(meaningLine(rec))
                                .font(.system(size: fontScale.size * 0.9))
                                .foregroundStyle(.primary)
                            Text(metaLine(rec))
                                .font(.system(size: fontScale.size * 0.8))
                                .foregroundStyle(.secondary)
                        }
                    }
                }
                .listStyle(.plain)

                if totalPages > 1 {
                    HStack {
                        Button("上一页") {
                            page = max(1, page - 1)
                        }
                        .disabled(page <= 1)
                        Spacer()
                        Text("\(page) / \(totalPages)")
                        Spacer()
                        Button("下一页") {
                            page = min(totalPages, page + 1)
                        }
                        .disabled(page >= totalPages)
                    }
                    .padding()
                }
            }
            .navigationTitle("中考词汇")
            .sheet(isPresented: $showFilter) {
                NavigationStack {
                    Form {
                        Picker("类型", selection: $filterType) {
                            Text("所有词汇").tag("ALL")
                            Text("中考词汇").tag("MIDDLE_SCHOOL")
                            Text("超纲词汇").tag("ADVANCED")
                        }
                    }
                    .navigationTitle("筛选")
                    .toolbar {
                        ToolbarItem(placement: .confirmationAction) {
                            Button("确定") {
                                page = 1
                                showFilter = false
                            }
                        }
                    }
                }
                .presentationDetents([.medium])
            }
            .sheet(item: Binding(
                get: { selectedWord.map { IdentifiableWord(id: $0) } },
                set: { selectedWord = $0?.id }
            )) { item in
                WordDetailSheet(word: item.id, context: .list)
                    .environmentObject(vocabulary)
                    .environmentObject(notebook)
                    .environment(\.fontScale, fontScale)
                    .presentationDetents([.medium, .large])
                    .presentationDragIndicator(.visible)
            }
        }
    }

    private var summaryText: String {
        "当前筛选共 \(filtered.count) 个 · 词库 \(vocabulary.totalCount) 个 · 每页 \(pageSize) 个"
    }

    private func meaningLine(_ rec: WordRecord) -> String {
        let parts = rec.definitions.map { d in
            let p = d.partOfSpeech.trimmingCharacters(in: .whitespaces)
            let m = d.meaning.trimmingCharacters(in: .whitespaces)
            return p.isEmpty ? m : "\(p) \(m)"
        }.filter { !$0.isEmpty }
        return parts.isEmpty ? "暂无释义" : parts.joined(separator: " · ")
    }

    private func metaLine(_ rec: WordRecord) -> String {
        let t = rec.type == "ADVANCED" ? "超纲" : "中考"
        let ph = rec.phonetic.trimmingCharacters(in: .whitespaces)
        return ph.isEmpty ? t : "\(t) · \(ph)"
    }
}

private struct IdentifiableWord: Identifiable {
    let id: String
}
