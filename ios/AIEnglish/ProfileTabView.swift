import SwiftUI

struct ProfileTabView: View {
    @EnvironmentObject private var vocabulary: VocabularyStore
    @EnvironmentObject private var notebook: NotebookStore
    @Environment(\.fontScale) private var fontScale
    @State private var detailWord: String?
    @State private var confirmClear = false

    var body: some View {
        NavigationStack {
            List {
                Section {
                    if notebook.entries.isEmpty {
                        Text("生词本为空")
                            .foregroundStyle(.secondary)
                    } else {
                        ForEach(notebook.entries, id: \.word) { item in
                            Button {
                                detailWord = item.word
                            } label: {
                                HStack {
                                    Text(item.word)
                                        .font(.system(size: fontScale.size))
                                    Spacer()
                                    Text("\(item.count)次")
                                        .font(.system(size: fontScale.size * 0.9))
                                        .foregroundStyle(.secondary)
                                }
                            }
                        }
                        .onDelete(perform: deleteWords)
                    }
                } header: {
                    Text("生词本（\(notebook.entries.count) 个词 · 累计 \(notebook.entries.reduce(0) { $0 + $1.count }) 次）")
                } footer: {
                    Text("在词库详情或阅读结果中可加入生词本，数据保存在本机。")
                }

                Section {
                    Button("清空生词本", role: .destructive) {
                        confirmClear = true
                    }
                    .disabled(notebook.entries.isEmpty)
                }
            }
            .navigationTitle("我的")
            .confirmationDialog("确定清空全部生词？", isPresented: $confirmClear, titleVisibility: .visible) {
                Button("清空", role: .destructive) { notebook.clear() }
                Button("取消", role: .cancel) {}
            }
            .sheet(item: Binding(
                get: { detailWord.map { ProfileSheetWord(id: $0) } },
                set: { detailWord = $0?.id }
            )) { item in
                WordDetailSheet(word: item.id, context: .notebook)
                    .environmentObject(vocabulary)
                    .environmentObject(notebook)
                    .environment(\.fontScale, fontScale)
                    .presentationDetents([.medium, .large])
                    .presentationDragIndicator(.visible)
            }
        }
    }

    private func deleteWords(at offsets: IndexSet) {
        for i in offsets {
            let w = notebook.entries[i].word
            notebook.remove(w)
        }
    }
}

private struct ProfileSheetWord: Identifiable {
    let id: String
}
