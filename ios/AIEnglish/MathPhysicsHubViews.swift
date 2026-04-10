import SwiftUI

/// 数学 Tab：试卷结构 + 历年真题
struct MathHubView: View {
    private enum Sub: Hashable {
        case structure
        case past
    }

    @State private var sub: Sub = .structure

    var body: some View {
        VStack(spacing: 0) {
            Picker("", selection: $sub) {
                Text("试卷结构").tag(Sub.structure)
                Text("历年真题").tag(Sub.past)
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
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

/// 物理 Tab：试卷结构 + 中考真题
struct PhysicsHubView: View {
    private enum Sub: Hashable {
        case structure
        case zhongkao
    }

    @State private var sub: Sub = .structure

    var body: some View {
        VStack(spacing: 0) {
            Picker("", selection: $sub) {
                Text("试卷结构").tag(Sub.structure)
                Text("中考真题").tag(Sub.zhongkao)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color(.secondarySystemGroupedBackground))

            Group {
                switch sub {
                case .structure:
                    PhysicsTabView()
                case .zhongkao:
                    PhysicsZhongkaoTabView()
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}
