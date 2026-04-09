import SwiftUI

private struct DaofaSection: Identifiable, Hashable {
    var id: String { title }
    let title: String
    let body: String
}

private struct DaofaPastExamsFile: Codable {
    let version: Int?
    let label: String?
    let items: [DaofaPastExamItem]
}

private struct DaofaPastExamItem: Codable, Identifiable, Hashable {
    let id: String
    let title: String
    let body: String
}

/// 道法：试卷结构 + 主观题 + 历年中考题（列表/详情与英语作文交互一致，无朗读）。
struct DaofaTabView: View {
    private enum Sub: Hashable {
        case structure
        case subjective
        case past
    }

    @Environment(\.fontScale) private var fontScale
    @State private var sub: Sub = .structure
    @State private var sections: [DaofaSection] = []
    @State private var detail: DaofaSection?
    @State private var pastItems: [DaofaPastExamItem] = []
    @State private var pastLabel: String = ""
    @State private var pastDetail: DaofaPastExamItem?
    @State private var loadError: String?
    @State private var dataReady = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                Picker("", selection: $sub) {
                    Text("试卷结构").tag(Sub.structure)
                    Text("主观题").tag(Sub.subjective)
                    Text("历年中考题").tag(Sub.past)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color(.secondarySystemGroupedBackground))
                .onChange(of: sub) { _ in
                    detail = nil
                    pastDetail = nil
                }

                Group {
                    if !dataReady {
                        ProgressView("加载中…")
                    } else {
                        switch sub {
                        case .structure:
                            DaofaStructureTabView()
                        case .subjective:
                            if let d = detail {
                                DaofaSubjectDetailView(section: d, barTitle: "道法主观题", onBack: { detail = nil })
                            } else {
                                VStack(alignment: .leading, spacing: 0) {
                                    if let err = loadError, sections.isEmpty {
                                        Text(err)
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                            .padding(.horizontal, 16)
                                            .padding(.top, 8)
                                            .padding(.bottom, 4)
                                    } else {
                                        Text("按板块浏览，点击条目查看全文（与英语作文相同版式）。")
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                            .padding(.horizontal, 16)
                                            .padding(.top, 8)
                                            .padding(.bottom, 4)
                                    }
                                    List {
                                        ForEach(sections) { sec in
                                            Button {
                                                detail = sec
                                            } label: {
                                                HStack(alignment: .center) {
                                                    Text(sec.title)
                                                        .font(.system(size: fontScale.size))
                                                        .foregroundStyle(.primary)
                                                        .multilineTextAlignment(.leading)
                                                        .frame(maxWidth: .infinity, alignment: .leading)
                                                    Text("›")
                                                        .foregroundStyle(.tertiary)
                                                        .font(.title3)
                                                }
                                            }
                                        }
                                    }
                                    .listStyle(.insetGrouped)
                                }
                            }
                        case .past:
                            if let pd = pastDetail {
                                DaofaSubjectDetailView(
                                    section: DaofaSection(title: pd.title, body: pd.body),
                                    barTitle: "历年中考题",
                                    onBack: { pastDetail = nil }
                                )
                            } else {
                                VStack(alignment: .leading, spacing: 0) {
                                    if !pastLabel.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                                        Text(pastLabel)
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                            .padding(.horizontal, 16)
                                            .padding(.top, 8)
                                            .padding(.bottom, 4)
                                    } else {
                                        Text("按条目浏览历年真题概览，点击查看全文（与主观题相同版式）。")
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                            .padding(.horizontal, 16)
                                            .padding(.top, 8)
                                            .padding(.bottom, 4)
                                    }
                                    if pastItems.isEmpty {
                                        Text("暂无历年中考题数据。")
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                            .padding(.horizontal, 16)
                                            .padding(.top, 8)
                                    } else {
                                        List {
                                            ForEach(pastItems) { item in
                                                Button {
                                                    pastDetail = item
                                                } label: {
                                                    HStack(alignment: .center) {
                                                        Text(item.title)
                                                            .font(.system(size: fontScale.size))
                                                            .foregroundStyle(.primary)
                                                            .multilineTextAlignment(.leading)
                                                            .frame(maxWidth: .infinity, alignment: .leading)
                                                        Text("›")
                                                            .foregroundStyle(.tertiary)
                                                            .font(.title3)
                                                    }
                                                }
                                            }
                                        }
                                        .listStyle(.insetGrouped)
                                    }
                                }
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
            .navigationTitle("道法")
        }
        .task {
            await load()
        }
    }

    private func load() async {
        defer { dataReady = true }
        guard let urlR = Bundle.main.url(forResource: "daofa_reference", withExtension: "txt") else {
            loadError = "缺少 daofa_reference.txt"
            return
        }
        do {
            let ref = try String(contentsOf: urlR, encoding: .utf8)
            sections = parseDaofaReference(ref)
            if let urlP = Bundle.main.url(forResource: "daofa_past_exams", withExtension: "json") {
                let data = try Data(contentsOf: urlP)
                let past = try JSONDecoder().decode(DaofaPastExamsFile.self, from: data)
                pastItems = past.items
                pastLabel = past.label ?? ""
            }
        } catch {
            loadError = error.localizedDescription
        }
    }

    private func parseDaofaReference(_ text: String) -> [DaofaSection] {
        let lines = text.components(separatedBy: .newlines)
        var out: [DaofaSection] = []
        var i = 0
        while i < lines.count {
            let t = lines[i].trimmingCharacters(in: .whitespaces)
            if t.hasPrefix("📚") || t.hasPrefix("📊") {
                let title = t
                var bodyLines: [String] = []
                i += 1
                while i < lines.count {
                    let lt = lines[i].trimmingCharacters(in: .whitespaces)
                    if lt.hasPrefix("📚") || lt.hasPrefix("📊") { break }
                    bodyLines.append(lines[i])
                    i += 1
                }
                let body = bodyLines.joined(separator: "\n").trimmingCharacters(in: .whitespacesAndNewlines)
                out.append(DaofaSection(title: title, body: body))
            } else {
                i += 1
            }
        }
        return out
    }
}

private struct DaofaSubjectDetailView: View {
    let section: DaofaSection
    var barTitle: String = "道法主观题"
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
                    Text(barTitle)
                        .font(.subheadline.weight(.semibold))
                    Text(section.title)
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
                    Text(section.title)
                        .font(.title3.weight(.bold))
                    Text("正文")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.secondary)
                    Text(section.body)
                        .font(.system(size: fontScale.size))
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .textSelection(.enabled)
                }
                .padding(16)
            }
        }
    }
}
