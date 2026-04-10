import SwiftUI

/// 冷启动后的闪屏：圆形 Logo + 标语（系统 UILaunchScreen 无法渲染文字，故用 SwiftUI 衔接）
struct SplashScreenView: View {
    @Binding var showSplash: Bool

    private static let slogan = "抓紧时间，掌握技巧，中考必胜！"
    private static let displayNanoseconds: UInt64 = 1_800_000_000

    var body: some View {
        ZStack {
            Color("LaunchScreenBackground")
                .ignoresSafeArea()

            VStack(spacing: 28) {
                Image("LaunchLogo")
                    .resizable()
                    .scaledToFill()
                    .frame(width: 128, height: 128)
                    .clipShape(Circle())
                    .overlay(
                        Circle()
                            .strokeBorder(Color.white.opacity(0.45), lineWidth: 2)
                    )
                    .shadow(color: .black.opacity(0.12), radius: 16, y: 6)
                    .accessibilityHidden(true)

                Text(Self.slogan)
                    .font(.system(size: 18, weight: .semibold, design: .rounded))
                    .multilineTextAlignment(.center)
                    .foregroundStyle(Color.primary)
                    .padding(.horizontal, 36)
                    .accessibilityLabel(Self.slogan)
            }
        }
        .task {
            try? await Task.sleep(nanoseconds: Self.displayNanoseconds)
            withAnimation(.easeOut(duration: 0.4)) {
                showSplash = false
            }
        }
    }
}

#Preview {
    SplashScreenView(showSplash: .constant(true))
}
