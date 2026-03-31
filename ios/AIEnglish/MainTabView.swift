import SwiftUI

struct MainTabView: View {
    @StateObject private var vocabulary = VocabularyStore()
    @StateObject private var notebook = NotebookStore()
    @AppStorage("aienglish_font") private var fontScale: String = "standard"

    var body: some View {
        TabView {
            VocabularyTabView()
                .environmentObject(vocabulary)
                .environmentObject(notebook)
                .tabItem { Label("词库", systemImage: "books.vertical") }

            ScanTabView()
                .environmentObject(vocabulary)
                .environmentObject(notebook)
                .tabItem { Label("扫描", systemImage: "magnifyingglass") }

            ProfileTabView()
                .environmentObject(vocabulary)
                .environmentObject(notebook)
                .tabItem { Label("我的", systemImage: "person") }
        }
        .environment(\.fontScale, FontScale(rawValue: fontScale) ?? .standard)
    }
}

enum FontScale: String {
    case small, standard, large, xlarge
    var size: CGFloat {
        switch self {
        case .small: return 14
        case .standard: return 16
        case .large: return 18
        case .xlarge: return 20
        }
    }
}

private struct FontScaleKey: EnvironmentKey {
    static let defaultValue: FontScale = .standard
}

extension EnvironmentValues {
    var fontScale: FontScale {
        get { self[FontScaleKey.self] }
        set { self[FontScaleKey.self] = newValue }
    }
}
