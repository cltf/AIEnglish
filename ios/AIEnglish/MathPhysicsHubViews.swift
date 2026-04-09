import SwiftUI

/// 数学 Tab：试卷结构 + 历年真题 + 备考要点（占位）
struct MathHubView: View {
    private enum Sub: Hashable {
        case structure
        case past
        case extra
    }

    @State private var sub: Sub = .structure

    var body: some View {
        VStack(spacing: 0) {
            Picker("", selection: $sub) {
                Text("试卷结构").tag(Sub.structure)
                Text("历年真题").tag(Sub.past)
                Text("备考要点").tag(Sub.extra)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color(.secondarySystemGroupedBackground))

            Group {
                switch sub {
                case .structure:
                    MathTabView()
                case .past:
                    MathZhongkaoTabView()
                case .extra:
                    Text("更多数学内容敬请期待。")
                        .foregroundStyle(.secondary)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

/// 物理 Tab：试卷结构 + 备考要点（占位）
struct PhysicsHubView: View {
    private enum Sub: Hashable {
        case structure
        case extra
    }

    @State private var sub: Sub = .structure

    var body: some View {
        VStack(spacing: 0) {
            Picker("", selection: $sub) {
                Text("试卷结构").tag(Sub.structure)
                Text("备考要点").tag(Sub.extra)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color(.secondarySystemGroupedBackground))

            Group {
                switch sub {
                case .structure:
                    PhysicsTabView()
                case .extra:
                    Text("更多物理内容敬请期待。")
                        .foregroundStyle(.secondary)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}
