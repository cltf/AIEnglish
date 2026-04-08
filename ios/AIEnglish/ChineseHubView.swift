import SwiftUI

/// 语文 Tab：作文 + 中考真题
struct ChineseHubView: View {
    private enum Sub: Hashable {
        case essay
        case zhongkao
    }

    @State private var sub: Sub = .essay

    var body: some View {
        VStack(spacing: 0) {
            Picker("", selection: $sub) {
                Text("作文").tag(Sub.essay)
                Text("中考真题").tag(Sub.zhongkao)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color(.secondarySystemGroupedBackground))

            Group {
                switch sub {
                case .essay:
                    EssayTabView(fixedSubject: "chinese")
                case .zhongkao:
                    ChineseZhongkaoTabView()
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}
