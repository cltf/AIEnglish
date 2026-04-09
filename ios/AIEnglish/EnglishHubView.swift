import SwiftUI

/// 英语 Tab：试卷结构 + 词库、阅读、作文等
struct EnglishHubView: View {
    private enum Sub: String, CaseIterable {
        case structure
        case vocab
        case scan
        case readinghf
        case readingskills
        case mc688
        case essay
    }

    @State private var sub: Sub = .vocab
    @EnvironmentObject private var vocabulary: VocabularyStore
    @EnvironmentObject private var notebook: NotebookStore

    var body: some View {
        VStack(spacing: 0) {
            Picker("", selection: $sub) {
                Text("试卷结构").tag(Sub.structure)
                Text("词库").tag(Sub.vocab)
                Text("英语阅读").tag(Sub.scan)
                Text("阅读高频").tag(Sub.readinghf)
                Text("阅读技巧").tag(Sub.readingskills)
                Text("21天688").tag(Sub.mc688)
                Text("英语作文").tag(Sub.essay)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color(.secondarySystemGroupedBackground))

            Group {
                switch sub {
                case .structure:
                    EnglishStructureTabView()
                case .vocab:
                    VocabularyTabView()
                        .environmentObject(vocabulary)
                        .environmentObject(notebook)
                case .scan:
                    ScanTabView()
                        .environmentObject(vocabulary)
                        .environmentObject(notebook)
                case .readinghf:
                    ReadingHighFreqTabView()
                        .environmentObject(notebook)
                case .readingskills:
                    ReadingSkillsTabView()
                case .mc688:
                    Mc688TabView()
                        .environmentObject(notebook)
                case .essay:
                    EssayTabView(fixedSubject: "english")
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}
