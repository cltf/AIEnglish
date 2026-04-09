import SwiftUI
import UIKit

struct MainTabView: View {
    @StateObject private var vocabulary = VocabularyStore()
    @StateObject private var notebook = NotebookStore()
    @AppStorage("aienglish_font") private var fontScale: String = "standard"
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    @State private var selectedTab = 0

    private let tabs: [(icon: String, title: String)] = [
        ("doc.text", "语文"),
        ("sum", "数学"),
        ("book", "英语"),
        ("function", "物理"),
        ("list.clipboard", "道法"),
        ("person", "我的"),
    ]

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                Group {
                    switch selectedTab {
                    case 0:
                        ChineseHubView()
                    case 1:
                        MathHubView()
                    case 2:
                        EnglishHubView()
                            .environmentObject(vocabulary)
                            .environmentObject(notebook)
                    case 3:
                        PhysicsHubView()
                    case 4:
                        DaofaTabView()
                    case 5:
                        ProfileTabView()
                            .environmentObject(vocabulary)
                            .environmentObject(notebook)
                    default:
                        ChineseHubView()
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .environment(\.horizontalSizeClass, tabBarHorizontalSizeClass)
                .environment(\.fontScale, FontScale(rawValue: fontScale) ?? .standard)

                customBottomBar
            }

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
                        .padding(.bottom, 72)
                }
                .transition(.opacity)
                .animation(.easeInOut(duration: 0.2), value: notebook.toastMessage)
                .allowsHitTesting(false)
            }
        }
    }

    private var customBottomBar: some View {
        VStack(spacing: 0) {
            Divider()
            HStack(spacing: 0) {
                ForEach(Array(tabs.enumerated()), id: \.offset) { index, tab in
                    Button {
                        selectedTab = index
                    } label: {
                        VStack(spacing: 3) {
                            Image(systemName: tab.icon)
                                .font(.system(size: 20))
                            Text(tab.title)
                                .font(.system(size: 10, weight: selectedTab == index ? .semibold : .regular))
                                .lineLimit(1)
                                .minimumScaleFactor(0.75)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 6)
                        .contentShape(Rectangle())
                        .foregroundStyle(selectedTab == index ? Color.accentColor : Color.secondary)
                    }
                    .buttonStyle(.plain)
                    .accessibilityLabel(tab.title)
                }
            }
            .padding(.bottom, 2)
            .background(Color(.systemBackground))
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
