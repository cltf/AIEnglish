import SwiftUI
import UIKit

struct MainTabView: View {
    @StateObject private var vocabulary = VocabularyStore()
    @StateObject private var notebook = NotebookStore()
    @AppStorage("aienglish_font") private var fontScale: String = "standard"
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass

    var body: some View {
        ZStack {
            TabView {
                ChineseHubView()
                    .tabItem { Label("语文", systemImage: "doc.text") }

                EnglishHubView()
                    .environmentObject(vocabulary)
                    .environmentObject(notebook)
                    .tabItem { Label("英语", systemImage: "book") }

                DaofaTabView()
                    .tabItem { Label("道法", systemImage: "list.clipboard") }

                ProfileTabView()
                    .environmentObject(vocabulary)
                    .environmentObject(notebook)
                    .tabItem { Label("我的", systemImage: "person") }
            }
            /// iPad（尤其 iPadOS 18+）默认会把主导航放在顶部/侧边；对 TabView 使用 compact 横屏尺寸类可恢复底部标签栏。
            .environment(\.horizontalSizeClass, tabBarHorizontalSizeClass)
            .environment(\.fontScale, FontScale(rawValue: fontScale) ?? .standard)

            if let msg = notebook.toastMessage {
                VStack {
                    Spacer()
                    Text(msg)
                        .font(.subheadline.weight(.medium))
                        .foregroundStyle(.white)
                        .padding(.horizontal, 20)
                        .padding(.vertical, 12)
                        .background(Color.black.opacity(0.82))
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                        .padding(.bottom, 96)
                }
                .transition(.opacity)
                .animation(.easeInOut(duration: 0.2), value: notebook.toastMessage)
                .allowsHitTesting(false)
            }
        }
    }

    private var tabBarHorizontalSizeClass: UserInterfaceSizeClass {
        if UIDevice.current.userInterfaceIdiom == .pad {
            return .compact
        }
        return horizontalSizeClass ?? .compact
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
