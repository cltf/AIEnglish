import SwiftUI

struct EssayDetailView: View {
    let exam: EssayExam
    let sample: EssaySample
    @ObservedObject var ttsPlayer: EssayTTSPlayer
    @Environment(\.fontScale) private var fontScale

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                if let err = ttsPlayer.playbackError {
                    Text(err)
                        .font(.caption)
                        .foregroundStyle(.red)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(10)
                        .background(Color.red.opacity(0.1), in: RoundedRectangle(cornerRadius: 10, style: .continuous))
                }
                Text(sample.title)
                    .font(.title3.weight(.semibold))
                    .foregroundStyle(.primary)

                VStack(alignment: .leading, spacing: 8) {
                    Text("本年题目")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.tint)
                    Text(exam.topics)
                        .font(.system(size: fontScale.size))
                        .foregroundStyle(.primary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(14)
                .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 14, style: .continuous))

                EssayTTSControls(
                    sampleId: sample.id,
                    ttsPlayer: ttsPlayer
                )

                VStack(alignment: .leading, spacing: 8) {
                    Text("正文")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.secondary)
                    Text(sample.body.trimmingCharacters(in: .whitespacesAndNewlines))
                        .font(.system(size: fontScale.size))
                        .lineSpacing(5)
                        .foregroundStyle(.primary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(14)
                .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 14, style: .continuous))
            }
            .padding(16)
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("范文详情")
        .navigationBarTitleDisplayMode(.inline)
    }
}
