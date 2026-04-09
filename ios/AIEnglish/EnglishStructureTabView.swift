import SwiftUI

private struct EnglishStructureFile: Codable {
    let version: Int?
    let title: String
    let subtitle: String?
    let badge: String?
    let sections: [EnglishStructureSection]
}

private struct EnglishStructureSection: Codable {
    let title: String
    let blocks: [EnglishStructureBlock]
}

private struct EnglishStructureBlock: Codable {
    let type: String
    let text: String?
    let title: String?
    let headers: [String]?
    let rows: [[String]]?
    let items: [String]?
    let pairs: [[String]]?
}

/// 北京中考英语试卷结构（english_beijing_structure.json）
struct EnglishStructureTabView: View {
    @Environment(\.fontScale) private var fontScale
    @State private var doc: EnglishStructureFile?
    @State private var loadError: String?

    var body: some View {
        Group {
            if let err = loadError, doc == nil {
                Text(err).foregroundStyle(.secondary).padding()
            } else if doc == nil {
                ProgressView("加载中…")
            } else if let file = doc {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        VStack(alignment: .leading, spacing: 6) {
                            Text(file.title)
                                .font(.title2.weight(.bold))
                            if let sub = file.subtitle, !sub.isEmpty {
                                Text(sub)
                                    .font(.title3.weight(.medium))
                                    .foregroundStyle(.secondary)
                            }
                            if let b = file.badge, !b.isEmpty {
                                Text(b)
                                    .font(.subheadline.weight(.medium))
                                    .foregroundStyle(.primary)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(Color.accentColor.opacity(0.14))
                                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                            }
                        }
                        ForEach(Array(file.sections.enumerated()), id: \.offset) { _, sec in
                            EnglishStructureSectionCard(section: sec, fontScale: fontScale)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                }
            }
        }
        .task { await load() }
    }

    private func load() async {
        guard let url = Bundle.main.url(forResource: "english_beijing_structure", withExtension: "json")
                ?? Bundle.main.url(forResource: "english_beijing_structure", withExtension: "json", subdirectory: "web/data") else {
            loadError = "缺少 english_beijing_structure.json"
            return
        }
        do {
            let data = try Data(contentsOf: url)
            doc = try JSONDecoder().decode(EnglishStructureFile.self, from: data)
        } catch {
            loadError = error.localizedDescription
        }
    }
}

private struct EnglishStructureSectionCard: View {
    let section: EnglishStructureSection
    let fontScale: FontScale

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(section.title)
                .font(.headline.weight(.semibold))
                .foregroundStyle(Color.accentColor)
            VStack(alignment: .leading, spacing: 10) {
                ForEach(Array(section.blocks.enumerated()), id: \.offset) { _, block in
                    EnglishStructureBlockView(block: block, fontScale: fontScale)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .fill(Color(.secondarySystemGroupedBackground))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(Color(.separator).opacity(0.35), lineWidth: 0.5)
        )
    }
}

private struct EnglishStructureBlockView: View {
    let block: EnglishStructureBlock
    let fontScale: FontScale

    var body: some View {
        Group {
            switch block.type {
            case "table":
                EnglishStructureTableView(headers: block.headers ?? [], rows: block.rows ?? [], fontScale: fontScale)
            case "subheading":
                Text(block.text ?? "")
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.primary)
                    .padding(.top, 4)
            case "label":
                Text(block.text ?? "")
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(.orange)
            case "bullets":
                VStack(alignment: .leading, spacing: 6) {
                    ForEach(Array((block.items ?? []).enumerated()), id: \.offset) { _, line in
                        HStack(alignment: .top, spacing: 8) {
                            Text("•").fontWeight(.bold).foregroundStyle(Color.accentColor)
                            Text(line)
                                .font(.system(size: fontScale.size))
                                .foregroundStyle(.secondary)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                }
            case "keyValues":
                VStack(alignment: .leading, spacing: 8) {
                    ForEach(Array((block.pairs ?? []).enumerated()), id: \.offset) { _, pair in
                        if pair.count >= 2 {
                            HStack(alignment: .top, spacing: 10) {
                                Text(pair[0])
                                    .font(.caption.weight(.semibold))
                                    .foregroundStyle(.tertiary)
                                    .frame(minWidth: 72, maxWidth: 120, alignment: .leading)
                                Text(pair[1])
                                    .font(.system(size: fontScale.size))
                                    .foregroundStyle(.primary)
                                    .fixedSize(horizontal: false, vertical: true)
                            }
                        }
                    }
                }
            case "subsection":
                Text(block.title ?? "")
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.secondary)
                    .padding(.top, 6)
            case "callout":
                VStack(alignment: .leading, spacing: 8) {
                    ForEach(Array((block.items ?? []).enumerated()), id: \.offset) { _, line in
                        HStack(alignment: .top, spacing: 8) {
                            Text("◆").font(.caption2).foregroundStyle(.tertiary)
                            Text(line)
                                .font(.system(size: fontScale.size))
                                .foregroundStyle(.primary)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                }
                .padding(12)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color.accentColor.opacity(0.08))
                )
            default:
                if let t = block.text, !t.isEmpty {
                    Text(t).font(.system(size: fontScale.size)).foregroundStyle(.secondary)
                }
            }
        }
    }
}

private struct EnglishStructureTableView: View {
    let headers: [String]
    let rows: [[String]]
    let fontScale: FontScale

    var body: some View {
        if headers.isEmpty || rows.isEmpty { EmptyView() }
        else {
            let colCount = headers.count
            VStack(spacing: 0) {
                HStack(spacing: 0) {
                    ForEach(Array(headers.enumerated()), id: \.offset) { _, h in
                        Text(h)
                            .font(.caption.weight(.semibold))
                            .foregroundStyle(.primary)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(10)
                            .background(Color.accentColor.opacity(0.12))
                    }
                }
                Divider()
                ForEach(Array(rows.enumerated()), id: \.offset) { ri, row in
                    HStack(spacing: 0) {
                        ForEach(0..<colCount, id: \.self) { ci in
                            Text(row.indices.contains(ci) ? row[ci] : "")
                                .font(.system(size: max(12, fontScale.size - 2)))
                                .foregroundStyle(.secondary)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(10)
                                .background(ri.isMultiple(of: 2) ? Color.clear : Color(.tertiarySystemFill).opacity(0.35))
                        }
                    }
                    if ri < rows.count - 1 {
                        Divider().opacity(0.5)
                    }
                }
            }
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .stroke(Color(.separator).opacity(0.5), lineWidth: 0.5)
            )
        }
    }
}
