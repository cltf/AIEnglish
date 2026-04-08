import Foundation
import UIKit

/// 调用本地 Go 代理（与 web/js 一致）
enum AIService {
    /// 默认模拟器连本机 Mac；真机调试请改为 Mac 的局域网 IP，例如 `http://192.168.1.5:8787`（Info.plist 键 `AIProxyBaseURL`）。
    private static var proxyBaseURL: URL {
        let fallback = URL(string: "http://127.0.0.1:8787")!
        guard let raw = Bundle.main.object(forInfoDictionaryKey: "AIProxyBaseURL") as? String else { return fallback }
        let s = raw.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !s.isEmpty, let u = URL(string: s) else { return fallback }
        return u
    }

    private static func proxyURL(path: String) -> URL {
        var base = proxyBaseURL.absoluteString
        while base.hasSuffix("/") { base.removeLast() }
        let p = path.hasPrefix("/") ? path : "/" + path
        if let u = URL(string: base + p) { return u }
        return URL(string: "http://127.0.0.1:8787" + p)!
    }

    private static var chatCompletionsURL: URL {
        proxyURL(path: "/openai-compatible/v1/chat/completions")
    }

    private static var essayTTSURL: URL {
        proxyURL(path: "/openai-compatible/v1/essay-tts")
    }

    static let defaultReadModel = "gemini-3-pro"
    static let defaultOcrModel = "gemini-2.5-flash-image"

    private static func postJSON(_ body: [String: Any]) async throws -> [String: Any] {
        var req = URLRequest(url: chatCompletionsURL)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.httpBody = try JSONSerialization.data(withJSONObject: body)
        let (data, resp) = try await URLSession.shared.data(for: req)
        guard let http = resp as? HTTPURLResponse, (200...299).contains(http.statusCode) else {
            let t = String(data: data, encoding: .utf8) ?? ""
            throw NSError(domain: "AI", code: (resp as? HTTPURLResponse)?.statusCode ?? -1, userInfo: [NSLocalizedDescriptionKey: String(t.prefix(400))])
        }
        let obj = try JSONSerialization.jsonObject(with: data) as? [String: Any]
        return obj ?? [:]
    }

    private static func contentString(from chat: [String: Any]) -> String {
        guard let choices = chat["choices"] as? [[String: Any]],
              let msg = choices.first?["message"] as? [String: Any] else { return "" }
        if let s = msg["content"] as? String { return s }
        if let parts = msg["content"] as? [[String: Any]] {
            return parts.compactMap { $0["text"] as? String }.joined()
        }
        return ""
    }

    static func transcribeImage(_ image: UIImage, ocrModel: String) async throws -> String {
        guard let jpeg = image.jpegData(compressionQuality: 0.92) else {
            throw NSError(domain: "AI", code: -2, userInfo: [NSLocalizedDescriptionKey: "图片编码失败"])
        }
        let dataUrl = "data:image/jpeg;base64,\(jpeg.base64EncodedString())"
        let model = ocrModel.isEmpty ? defaultOcrModel : ocrModel
        let body: [String: Any] = [
            "model": model,
            "messages": [
                ["role": "system", "content": "You transcribe English text from photos of textbooks and exam papers. Output only the English text from the image, preserving line breaks where natural. Do not translate or explain."],
                ["role": "user", "content": [
                    ["type": "text", "text": "Extract all English text from this image. If there is no English, output an empty string."],
                    ["type": "image_url", "image_url": ["url": dataUrl]],
                ]],
            ],
            "temperature": 0.1,
            "max_tokens": 8192,
        ]
        let json = try await postJSON(body)
        return contentString(from: json).trimmingCharacters(in: .whitespacesAndNewlines)
    }

    static func analyzeReading(text: String, readModel: String) async throws -> String {
        let sample = text.count > 12000 ? String(text.prefix(12000)) + "\n\n…（已截断）" : text
        let prompt = """
        以下英文可能来自 OCR。请用中文完成分析，并只输出一个 JSON 对象（不要使用 markdown 代码围栏），键名必须完全一致：
        {
          "paragraphGists": [{"index":1,"gistZh":"该段主旨的中文概括","keySentenceEn":"从原文摘一句最能代表该段的英文原句"}],
          "coreViewpointZh":"全文核心观点（中文）",
          "examPoints": ["中考阅读高频考点提示1","提示2"],
          "logicRelations": ["如：因果/转折/例证 与答题注意"],
          "howToSolveZh":"针对此类文章与常见设问，学生应如何审题与作答（分条，用\\\\n换行）"
        }
        英文文本：
        ---
        \(sample)
        ---
        """
        let model = readModel.isEmpty ? defaultReadModel : readModel
        let body: [String: Any] = [
            "model": model,
            "messages": [
                ["role": "system", "content": "你是中考英语阅读教研助手，只输出合法 JSON，不要任何解释文字。"],
                ["role": "user", "content": prompt],
            ],
            "temperature": 0.35,
        ]
        let json = try await postJSON(body)
        return contentString(from: json).trimmingCharacters(in: .whitespacesAndNewlines)
    }

    static func synthesizeEssayAudio(text: String, voiceName: String = "Kore") async throws -> Data {
        var req = URLRequest(url: essayTTSURL)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.httpBody = try JSONSerialization.data(withJSONObject: [
            "model": "gemini-2.5-flash-tts",
            "voiceName": voiceName,
            "text": text
        ])
        let (data, resp) = try await URLSession.shared.data(for: req)
        guard let http = resp as? HTTPURLResponse, (200...299).contains(http.statusCode) else {
            let t = String(data: data, encoding: .utf8) ?? ""
            throw NSError(domain: "AI", code: (resp as? HTTPURLResponse)?.statusCode ?? -1, userInfo: [NSLocalizedDescriptionKey: String(t.prefix(400))])
        }
        if data.isEmpty {
            throw NSError(domain: "AI", code: -3, userInfo: [NSLocalizedDescriptionKey: "TTS 返回空音频"])
        }
        return data
    }
}
