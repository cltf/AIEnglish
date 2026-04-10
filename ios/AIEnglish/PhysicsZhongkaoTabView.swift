import SwiftUI
import UIKit

private struct PhysicsZhongkaoFile: Codable {
    let version: Int?
    let label: String?
    let items: [PhysicsZhongkaoItem]
}

private struct PhysicsZhongkaoItem: Codable, Identifiable, Hashable {
    let id: String
    let title: String
    let body: String
    let images: [String]?
}

/// 物理 · 中考真题（列表/详情与数学历年真题相同）
struct PhysicsZhongkaoTabView: View {
    @Environment(\.fontScale) private var fontScale
    @State private var file: PhysicsZhongkaoFile?
    @State private var loadError: String?
    @State private var detail: PhysicsZhongkaoItem?

    var body: some View {
        Group {
            if let err = loadError, file == nil {
                Text(err).foregroundStyle(.secondary).padding()
            } else if file == nil {
                ProgressView("加载中…")
            } else if let d = detail {
                PhysicsZkDetailView(item: d, onBack: { detail = nil })
            } else if file?.items.isEmpty != false {
                Text("暂无中考真题数据。").foregroundStyle(.secondary).padding()
            } else if let f = file {
                ScrollView {
                    VStack(alignment: .leading, spacing: 12) {
                        if let lab = f.label, !lab.isEmpty {
                            Text(lab).font(.caption).foregroundStyle(.secondary)
                        } else {
                            Text("按条目浏览，点击查看全文。")
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        ForEach(f.items) { item in
                            Button {
                                detail = item
                            } label: {
                                HStack {
                                    Text(item.title)
                                        .font(.system(size: fontScale.size))
                                        .foregroundStyle(.primary)
                                        .multilineTextAlignment(.leading)
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                    Text("›").foregroundStyle(.tertiary)
                                }
                                .padding()
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .background(Color(.secondarySystemGroupedBackground))
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding()
                }
            }
        }
        .task { await load() }
    }

    private func load() async {
        guard let url = Bundle.main.url(forResource: "physics_zhongkao", withExtension: "json") else {
            loadError = "缺少 physics_zhongkao.json"
            return
        }
        do {
            let data = try Data(contentsOf: url)
            file = try JSONDecoder().decode(PhysicsZhongkaoFile.self, from: data)
        } catch {
            loadError = error.localizedDescription
        }
    }
}

private struct PhysicsZkDetailView: View {
    let item: PhysicsZhongkaoItem
    var onBack: () -> Void
    @Environment(\.fontScale) private var fontScale
    @State private var zoomedUIImage: UIImage?

    private var zoomPresented: Binding<Bool> {
        Binding(
            get: { zoomedUIImage != nil },
            set: { if !$0 { zoomedUIImage = nil } }
        )
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 8) {
                Button(action: onBack) {
                    Image(systemName: "chevron.backward")
                        .font(.body.weight(.semibold))
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text("中考真题")
                        .font(.subheadline.weight(.semibold))
                    Text(item.title)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                        .lineLimit(2)
                }
                Spacer(minLength: 0)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            Divider()
            ScrollView {
                VStack(alignment: .leading, spacing: 14) {
                    Text(item.title)
                        .font(.title3.weight(.bold))
                    if !item.body.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        Text("正文")
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(.secondary)
                        Text(item.body)
                            .font(.system(size: fontScale.size))
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .textSelection(.enabled)
                    }
                    if let paths = item.images, !paths.isEmpty {
                        Text("试卷图（点击放大，双指可缩放）")
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(.secondary)
                            .padding(.top, item.body.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? 0 : 4)
                        ForEach(Array(paths.enumerated()), id: \.offset) { _, raw in
                            physicsExamImage(assetPath: raw)
                        }
                    }
                }
                .padding(.horizontal, 6)
                .padding(.vertical, 16)
            }
        }
        .fullScreenCover(isPresented: zoomPresented) {
            if let img = zoomedUIImage {
                PhysicsExamZoomCover(image: img) {
                    zoomedUIImage = nil
                }
            }
        }
    }

    @ViewBuilder
    private func physicsExamImage(assetPath: String) -> some View {
        if let u = Self.bundleURL(forAssetPath: assetPath),
           let uiImg = UIImage(contentsOfFile: u.path) {
            Image(uiImage: uiImg)
                .resizable()
                .scaledToFit()
                .frame(maxWidth: .infinity)
                .contentShape(Rectangle())
                .onTapGesture {
                    zoomedUIImage = uiImg
                }
        } else {
            let name = (assetPath as NSString).lastPathComponent
            Text("（未找到 \(name)，请将图片放入 web/data/physics2025/ 后重新编译）")
                .font(.caption)
                .foregroundStyle(.tertiary)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }

    private static func bundleURL(forAssetPath path: String) -> URL? {
        let parts = path.split(separator: "/").map(String.init)
        guard parts.count >= 2, let file = parts.last else { return nil }
        let subdir = parts.dropLast().joined(separator: "/")
        let base = (file as NSString).deletingPathExtension
        let ext = (file as NSString).pathExtension
        if let exact = Bundle.main.url(forResource: base, withExtension: ext, subdirectory: subdir) {
            return exact
        }
        if let flat = Bundle.main.url(forResource: base, withExtension: ext) {
            return flat
        }
        let expectedSuffix = "/" + subdir + "/" + file
        let candidates = Bundle.main.urls(forResourcesWithExtension: ext, subdirectory: nil) ?? []
        return candidates.first { $0.path.hasSuffix(expectedSuffix) || $0.lastPathComponent == file }
    }
}

private struct PhysicsExamZoomCover: View {
    let image: UIImage
    var onClose: () -> Void
    @State private var scale: CGFloat = 1
    @State private var baseScale: CGFloat = 1

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()
            Image(uiImage: image)
                .resizable()
                .scaledToFit()
                .scaleEffect(scale)
                .gesture(
                    MagnificationGesture()
                        .onChanged { value in
                            scale = baseScale * value
                        }
                        .onEnded { _ in
                            baseScale = min(max(scale, 1), 5)
                            scale = baseScale
                        }
                )
                .padding()
            VStack {
                HStack {
                    Spacer()
                    Button("完成") {
                        onClose()
                    }
                    .foregroundStyle(.white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                }
                .padding(.top, 8)
                Spacer()
            }
        }
    }
}
