import SwiftUI

private struct ChineseZhongkaoFile: Codable {
    let version: Int?
    let label: String?
    let items: [ChineseZhongkaoItem]
}

private struct ChineseZhongkaoItem: Codable, Identifiable, Hashable {
    let id: String
    let title: String
    let body: String
}

/// 语文 · 中考真题（列表/详情与道法历年题相同）
struct ChineseZhongkaoTabView: View {
    @Environment(\.fontScale) private var fontScale
    @State private var file: ChineseZhongkaoFile?
    @State private var loadError: String?
    @State private var detail: ChineseZhongkaoItem?

    var body: some View {
        Group {
            if let err = loadError, file == nil {
                Text(err).foregroundStyle(.secondary).padding()
            } else if file == nil {
                ProgressView("加载中…")
            } else if let d = detail {
                ChineseZkDetailView(item: d, onBack: { detail = nil })
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
        guard let url = Bundle.main.url(forResource: "chinese_zhongkao", withExtension: "json") else {
            loadError = "缺少 chinese_zhongkao.json"
            return
        }
        do {
            let data = try Data(contentsOf: url)
            file = try JSONDecoder().decode(ChineseZhongkaoFile.self, from: data)
        } catch {
            loadError = error.localizedDescription
        }
    }
}

private struct ChineseZkDetailView: View {
    let item: ChineseZhongkaoItem
    var onBack: () -> Void
    @Environment(\.fontScale) private var fontScale

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
                    Text("正文")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.secondary)
                    Text(item.body)
                        .font(.system(size: fontScale.size))
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .textSelection(.enabled)
                }
                .padding(16)
            }
        }
    }
}
