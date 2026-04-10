import SwiftUI

struct ContentView: View {
    @State private var showSplash = true

    var body: some View {
        ZStack {
            MainTabView()

            if showSplash {
                SplashScreenView(showSplash: $showSplash)
                    .transition(.opacity)
                    .zIndex(1)
            }
        }
    }
}

#Preview {
    ContentView()
}
