import SwiftUI

struct ProfileTabView: View {
    @EnvironmentObject private var vocabulary: VocabularyStore
    @EnvironmentObject private var notebook: NotebookStore
    @Environment(\.fontScale) private var fontScale
    @AppStorage("aienglish_font") private var fontRaw: String = FontScale.standard.rawValue
    @State private var detailWord: String?
    @State private var confirmClear = false

    var body: some View {
        NavigationStack {
            List {
                Section("显示") {
                    Picker("字号", selection: $fontRaw) {
                        Text("小").tag(FontScale.small.rawValue)
                        Text("标准").tag(FontScale.standard.rawValue)
                        Text("大").tag(FontScale.large.rawValue)
                        Text("特大").tag(FontScale.xlarge.rawValue)
                    }
                }

                Section {
                    if notebook.words.isEmpty {
                        Text("生词本为空")
                            .foregroundStyle(.secondary)
                    } else {
                        ForEach(notebook.words, id: \.self) { w in
                            Button(w) {
                                detailWord = w
                            }
                            .font(.system(size: fontScale.size))
                        }
                        .onDelete(perform: deleteWords)
                    }
                } header: {
                    Text("生词本（\(notebook.words.count)）")
                } footer: {
                    Text("在词库详情或扫描结果中可加入生词本，数据保存在本机。")
                }

                Section {
                    Button("清空生词本", role: .destructive) {
                        confirmClear = true
                    }
                    .disabled(notebook.words.isEmpty)
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
            let w = notebook.words[i]
            notebook.remove(w)
        }
    }
}

private struct ProfileSheetWord: Identifiable {
    let id: String
}
