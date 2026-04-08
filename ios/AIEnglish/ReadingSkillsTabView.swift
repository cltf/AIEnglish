import SwiftUI

private struct ReadingSkillsFile: Codable {
    let version: Int?
    let label: String?
    let intro: String?
    let topics: [ReadingSkillTopic]
}

private struct ReadingSkillTopic: Codable, Identifiable {
    let id: String
    let title: String
    let summary: String?
    let sections: [ReadingSkillSection]
}

private struct ReadingSkillSection: Codable {
    let subtitle: String?
    let paragraph: String?
    let bullets: [String]?
}

/// 中考英语阅读技巧
struct ReadingSkillsTabView: View {
    @Environment(\.fontScale) private var fontScale
    @State private var file: ReadingSkillsFile?
    @State private var loadError: String?

    var body: some View {
        Group {
            if let err = loadError, file == nil {
                Text(err).foregroundStyle(.secondary).padding()
            } else if file == nil {
                ProgressView("加载中…")
            } else if let f = file {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        if let lab = f.label, !lab.isEmpty {
                            Text(lab)
                                .font(.headline)
                        }
                        if let intro = f.intro, !intro.isEmpty {
                            Text(intro)
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        ForEach(f.topics) { t in
                            VStack(alignment: .leading, spacing: 10) {
                                Text(t.title)
                                    .font(.system(size: fontScale.size + 1, weight: .semibold))
                                    .foregroundStyle(Color.accentColor)
                                if let s = t.summary, !s.isEmpty {
                                    Text(s)
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                                ForEach(Array(t.sections.enumerated()), id: \.offset) { _, sec in
                                    sectionView(sec)
                                }
                            }
                            .padding()
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color(.secondarySystemGroupedBackground))
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                        }
                    }
                    .padding()
                }
            }
        }
        .task { await load() }
    }

    @ViewBuilder
    private func sectionView(_ sec: ReadingSkillSection) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            if let st = sec.subtitle, !st.isEmpty {
                Text(st)
                    .font(.subheadline.weight(.medium))
            }
            if let p = sec.paragraph, !p.isEmpty {
                Text(p)
                    .font(.system(size: fontScale.size))
                    .foregroundStyle(.primary)
            }
            if let bs = sec.bullets, !bs.isEmpty {
                ForEach(Array(bs.enumerated()), id: \.offset) { _, line in
                    HStack(alignment: .top, spacing: 6) {
                        Text("•")
                            .foregroundStyle(.secondary)
                        Text(line)
                            .font(.system(size: fontScale.size))
                            .foregroundStyle(.primary)
                    }
                }
            }
        }
        .padding(.top, 4)
    }

    private func load() async {
        guard let url = Bundle.main.url(forResource: "reading_skills_zhongkao", withExtension: "json") else {
            loadError = "缺少 reading_skills_zhongkao.json"
            return
        }
        do {
            let data = try Data(contentsOf: url)
            file = try JSONDecoder().decode(ReadingSkillsFile.self, from: data)
        } catch {
            loadError = error.localizedDescription
        }
    }
}
