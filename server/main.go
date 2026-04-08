// AI 网关本地代理：转发 OpenAI 兼容请求到 ai-svc.xue.xiwang.com，解决浏览器 CORS。
// 运行：cd server && go run .     默认监听 :8787
// 环境变量：PORT（监听端口，默认 8787）、XIWANG_BEARER（推荐）
package main

import (
	"bytes"
	"context"
	"encoding/base64"
	"encoding/binary"
	"encoding/json"
	"errors"
	"io"
	"log"
	"net/http"
	"os"
	"regexp"
	"strconv"
	"strings"
	"time"
	"unicode/utf8"
)

const (
	defaultListen = ":8787"
	upstreamURL   = "https://ai-svc.xue.xiwang.com/openai-compatible/v1/chat/completions"
	// 若未设置环境变量 XIWANG_BEARER 则使用（与仓库中原 curl 一致；生产请用环境变量覆盖）
	defaultBearer = "300000454:0e80b963c82d42f842aac3351f21b312"
	defaultTTSModel = "gemini-2.5-flash-tts"
	defaultVoice    = "Kore"
)

func main() {
	addr := os.Getenv("PORT")
	if addr == "" {
		addr = defaultListen
	} else if !strings.HasPrefix(addr, ":") {
		addr = ":" + addr
	}

	mux := http.NewServeMux()
	mux.HandleFunc("/openai-compatible/v1/chat/completions", proxyChatCompletions)
	mux.HandleFunc("/openai-compatible/v1/essay-tts", proxyEssayTTS)
	mux.HandleFunc("/health", func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte("ok"))
	})

	log.Printf("AI proxy listening on %s → %s", addr, upstreamURL)
	if err := http.ListenAndServe(addr, withCORS(mux)); err != nil {
		log.Fatal(err)
	}
}

func bearer() string {
	if v := strings.TrimSpace(os.Getenv("XIWANG_BEARER")); v != "" {
		return v
	}
	return defaultBearer
}

func withCORS(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		origin := r.Header.Get("Origin")
		if origin == "" {
			origin = "*"
		}
		w.Header().Set("Access-Control-Allow-Origin", origin)
		w.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization, api-key")
		w.Header().Set("Access-Control-Max-Age", "86400")
		if r.Method == http.MethodOptions {
			w.WriteHeader(http.StatusNoContent)
			return
		}
		next.ServeHTTP(w, r)
	})
}

var upstreamClient = &http.Client{Timeout: 120 * time.Second}

func proxyChatCompletions(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}

	body, err := io.ReadAll(http.MaxBytesReader(w, r.Body, 40<<20))
	if err != nil {
		http.Error(w, "read body: "+err.Error(), http.StatusBadRequest)
		return
	}

	req, err := http.NewRequestWithContext(r.Context(), http.MethodPost, upstreamURL, bytes.NewReader(body))
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	ct := r.Header.Get("Content-Type")
	if ct == "" {
		ct = "application/json"
	}
	req.Header.Set("Content-Type", ct)
	req.Header.Set("api-key", bearer())
	req.Header.Set("Authorization", "Bearer "+bearer())

	resp, err := upstreamClient.Do(req)
	if err != nil {
		http.Error(w, "upstream: "+err.Error(), http.StatusBadGateway)
		return
	}
	defer resp.Body.Close()

	for k, vv := range resp.Header {
		if strings.EqualFold(k, "Access-Control-Allow-Origin") {
			continue
		}
		for _, v := range vv {
			w.Header().Add(k, v)
		}
	}
	w.Header().Set("Access-Control-Allow-Origin", r.Header.Get("Origin"))
	if w.Header().Get("Access-Control-Allow-Origin") == "" {
		w.Header().Set("Access-Control-Allow-Origin", "*")
	}

	w.WriteHeader(resp.StatusCode)
	_, _ = io.Copy(w, resp.Body)
}

type ttsRequest struct {
	Text      string `json:"text"`
	VoiceName string `json:"voiceName"`
	Model     string `json:"model"`
}

var reEssayWordcountFooter = regexp.MustCompile(`\n*（全文约\d+字）\s*$`)

func stripEssayTTSFooter(s string) string {
	return strings.TrimSpace(reEssayWordcountFooter.ReplaceAllString(s, ""))
}

// splitChineseSentences splits one paragraph on 。！？； (must match Python chunk_tts_text).
func splitChineseSentences(para string) []string {
	para = strings.TrimSpace(para)
	if para == "" {
		return nil
	}
	var out []string
	var b strings.Builder
	for _, r := range para {
		b.WriteRune(r)
		if r == '。' || r == '！' || r == '？' || r == '；' {
			s := strings.TrimSpace(b.String())
			if s != "" {
				out = append(out, s)
			}
			b.Reset()
		}
	}
	if b.Len() > 0 {
		s := strings.TrimSpace(b.String())
		if s != "" {
			out = append(out, s)
		}
	}
	return out
}

// chunkEssayTTSText merges sentences into chunks under maxRunes (sentence boundaries first).
func chunkEssayTTSText(s string, maxRunes int) []string {
	s = strings.TrimSpace(s)
	if s == "" {
		return nil
	}
	if utf8.RuneCountInString(s) <= maxRunes {
		return []string{s}
	}
	var segments []string
	for _, para := range strings.Split(s, "\n\n") {
		para = strings.TrimSpace(para)
		if para == "" {
			continue
		}
		segments = append(segments, splitChineseSentences(para)...)
	}
	if len(segments) == 0 {
		r := []rune(s)
		if len(r) > maxRunes {
			return []string{string(r[:maxRunes])}
		}
		return []string{s}
	}
	var chunks []string
	var buf strings.Builder
	bufRunes := 0
	flushBuf := func() {
		if buf.Len() == 0 {
			return
		}
		chunks = append(chunks, strings.TrimSpace(buf.String()))
		buf.Reset()
		bufRunes = 0
	}
	for _, seg := range segments {
		segRunes := utf8.RuneCountInString(seg)
		if segRunes > maxRunes {
			flushBuf()
			rs := []rune(seg)
			for i := 0; i < len(rs); i += maxRunes {
				end := i + maxRunes
				if end > len(rs) {
					end = len(rs)
				}
				chunks = append(chunks, string(rs[i:end]))
			}
			continue
		}
		if bufRunes+segRunes <= maxRunes {
			buf.WriteString(seg)
			bufRunes += segRunes
		} else {
			flushBuf()
			buf.WriteString(seg)
			bufRunes = segRunes
		}
	}
	flushBuf()
	return chunks
}

func wavDataPayload(wav []byte) (pcm []byte, sampleRate int, channels int) {
	sampleRate, channels = 24000, 1
	if len(wav) < 12 {
		return wav, sampleRate, channels
	}
	if string(wav[0:4]) != "RIFF" || string(wav[8:12]) != "WAVE" {
		return wav, sampleRate, channels
	}
	off := 12
	for off+8 <= len(wav) {
		id := string(wav[off : off+4])
		sz := int(binary.LittleEndian.Uint32(wav[off+4 : off+8]))
		if off+8+sz > len(wav) {
			break
		}
		payload := wav[off+8 : off+8+sz]
		switch id {
		case "fmt ":
			if len(payload) >= 18 {
				channels = int(binary.LittleEndian.Uint16(payload[2:4]))
				sampleRate = int(binary.LittleEndian.Uint32(payload[4:8]))
			}
		case "data":
			return payload, sampleRate, channels
		}
		off += 8 + sz
		if sz%2 == 1 {
			off++
		}
	}
	return nil, sampleRate, channels
}

func callUpstreamEssayTTS(ctx context.Context, text, model, voice string) ([]byte, int, error) {
	payload := map[string]any{
		"model": model,
		"messages": []map[string]string{
			{
				"role":    "user",
				"content": "Please read the following essay naturally in one voice:\n" + text,
			},
		},
		"extra_body": map[string]any{
			"generationConfig": map[string]any{
				"responseModalities": []string{"AUDIO"},
				"maxOutputTokens":   8192,
				"speechConfig": map[string]any{
					"voiceConfig": map[string]any{
						"prebuiltVoiceConfig": map[string]any{
							"voiceName": voice,
						},
					},
				},
			},
		},
	}
	body, _ := json.Marshal(payload)
	req, err := http.NewRequestWithContext(ctx, http.MethodPost, upstreamURL, bytes.NewReader(body))
	if err != nil {
		return nil, 0, err
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("api-key", bearer())
	req.Header.Set("Authorization", "Bearer "+bearer())

	resp, err := upstreamClient.Do(req)
	if err != nil {
		return nil, 0, err
	}
	defer resp.Body.Close()
	raw, _ := io.ReadAll(resp.Body)
	return raw, resp.StatusCode, nil
}

func proxyEssayTTS(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}

	var in ttsRequest
	if err := json.NewDecoder(http.MaxBytesReader(w, r.Body, 2<<20)).Decode(&in); err != nil {
		http.Error(w, "bad json: "+err.Error(), http.StatusBadRequest)
		return
	}
	text := stripEssayTTSFooter(strings.TrimSpace(in.Text))
	if text == "" {
		http.Error(w, "text is required", http.StatusBadRequest)
		return
	}
	voice := strings.TrimSpace(in.VoiceName)
	if voice == "" {
		voice = defaultVoice
	}
	model := strings.TrimSpace(in.Model)
	if model == "" {
		model = defaultTTSModel
	}

	chunks := []string{text}
	if utf8.RuneCountInString(text) > 180 {
		chunks = chunkEssayTTSText(text, 180)
	}

	if len(chunks) == 1 {
		raw, status, err := callUpstreamEssayTTS(r.Context(), chunks[0], model, voice)
		if err != nil {
			http.Error(w, "upstream: "+err.Error(), http.StatusBadGateway)
			return
		}
		if status < 200 || status >= 300 {
			w.Header().Set("Content-Type", "application/json")
			w.WriteHeader(status)
			_, _ = w.Write(raw)
			return
		}
		audioBytes, err := extractAudioBytes(raw)
		if err != nil {
			log.Printf("tts decode failed: %v raw=%s", err, truncateUTF8(raw, 1200))
			http.Error(w, "tts decode: "+err.Error(), http.StatusBadGateway)
			return
		}
		audioBytes = ensurePlayableWAV(audioBytes, raw)
		w.Header().Set("Content-Type", "audio/wav")
		w.Header().Set("Cache-Control", "no-store")
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write(audioBytes)
		return
	}

	var pcmAll []byte
	rate := 24000
	ch := 1
	for i, chnk := range chunks {
		raw, status, err := callUpstreamEssayTTS(r.Context(), chnk, model, voice)
		if err != nil {
			http.Error(w, "upstream: "+err.Error(), http.StatusBadGateway)
			return
		}
		if status < 200 || status >= 300 {
			w.Header().Set("Content-Type", "application/json")
			w.WriteHeader(status)
			_, _ = w.Write(raw)
			return
		}
		audioBytes, err := extractAudioBytes(raw)
		if err != nil {
			log.Printf("tts decode failed (chunk %d/%d): %v", i+1, len(chunks), err)
			http.Error(w, "tts decode: "+err.Error(), http.StatusBadGateway)
			return
		}
		audioBytes = ensurePlayableWAV(audioBytes, raw)
		pcm, sr, nc := wavDataPayload(audioBytes)
		if pcm == nil {
			http.Error(w, "tts: empty pcm chunk", http.StatusBadGateway)
			return
		}
		if sr > 0 {
			rate = sr
		}
		if nc > 0 {
			ch = nc
		}
		if i > 0 {
			silenceBytes := int(float64(rate) * 0.22 * float64(ch) * 2)
			pcmAll = append(pcmAll, bytes.Repeat([]byte{0}, silenceBytes)...)
		}
		pcmAll = append(pcmAll, pcm...)
	}
	out := buildWAVPCM16LE(pcmAll, rate, ch)
	w.Header().Set("Content-Type", "audio/wav")
	w.Header().Set("Cache-Control", "no-store")
	w.WriteHeader(http.StatusOK)
	_, _ = w.Write(out)
}

func truncateUTF8(b []byte, max int) string {
	if len(b) <= max {
		return string(b)
	}
	return string(b[:max]) + "...(truncated)"
}

func extractAudioBytes(raw []byte) ([]byte, error) {
	var m map[string]any
	if err := json.Unmarshal(raw, &m); err != nil {
		return nil, err
	}
	choices, ok := m["choices"].([]any)
	if !ok || len(choices) == 0 {
		return nil, errors.New("choices missing")
	}
	first, ok := choices[0].(map[string]any)
	if !ok {
		return nil, errors.New("choice format invalid")
	}
	msg, ok := first["message"].(map[string]any)
	if !ok {
		return nil, errors.New("message missing")
	}

	// Common format: choices[0].message.audio.data
	if audio, ok := msg["audio"].(map[string]any); ok {
		if data, ok := audio["data"].(string); ok {
			if b, err := decodeBase64Bytes(data); err == nil {
				return b, nil
			}
		}
	}

	// Fallback: some providers place audio as inline_data / inlineData / data:audio URLs.
	// Collect all chunks and concatenate to avoid returning only the first frame.
	var chunks [][]byte
	collectAudioBytes(msg, false, &chunks)
	if len(chunks) == 0 {
		collectAudioBytes(first, false, &chunks)
	}
	if len(chunks) == 0 {
		collectAudioBytes(m, false, &chunks)
	}
	if len(chunks) > 0 {
		total := 0
		for _, c := range chunks {
			total += len(c)
		}
		out := make([]byte, 0, total)
		for _, c := range chunks {
			out = append(out, c...)
		}
		return out, nil
	}
	return nil, errors.New("audio missing")
}

func decodeBase64Bytes(s string) ([]byte, error) {
	data := strings.TrimSpace(s)
	if data == "" {
		return nil, errors.New("empty base64")
	}
	if b, err := base64.StdEncoding.DecodeString(data); err == nil {
		return b, nil
	}
	if b, err := base64.RawStdEncoding.DecodeString(data); err == nil {
		return b, nil
	}
	return nil, errors.New("base64 decode failed")
}

func collectAudioBytes(v any, audioContext bool, out *[][]byte) {
	switch x := v.(type) {
	case map[string]any:
		// Enter audio context based on key/value hints.
		localAudio := audioContext
		if mt, _ := x["mimeType"].(string); strings.HasPrefix(strings.ToLower(mt), "audio/") {
			localAudio = true
		}
		if t, _ := x["type"].(string); strings.Contains(strings.ToLower(t), "audio") {
			localAudio = true
		}
		if data, _ := x["data"].(string); localAudio && strings.TrimSpace(data) != "" {
			if b, err := decodeBase64Bytes(data); err == nil {
				*out = append(*out, b)
			}
		}
		for k, vv := range x {
			keyAudio := localAudio
			kl := strings.ToLower(k)
			if strings.Contains(kl, "audio") || kl == "inlinedata" || kl == "inline_data" {
				keyAudio = true
			}
			collectAudioBytes(vv, keyAudio, out)
		}
	case []any:
		for _, vv := range x {
			collectAudioBytes(vv, audioContext, out)
		}
	case string:
		if b, ok := decodeAudioDataURL(x); ok {
			*out = append(*out, b)
		}
	}
}

func decodeAudioDataURL(s string) ([]byte, bool) {
	v := strings.TrimSpace(s)
	if !strings.HasPrefix(strings.ToLower(v), "data:audio/") {
		return nil, false
	}
	i := strings.Index(v, ",")
	if i <= 0 {
		return nil, false
	}
	meta := strings.ToLower(v[:i])
	if !strings.Contains(meta, ";base64") {
		return nil, false
	}
	b, err := decodeBase64Bytes(v[i+1:])
	if err != nil {
		return nil, false
	}
	return b, true
}

var rePCMRate = regexp.MustCompile(`rate=(\d+)`)

// Upstream TTS often returns raw PCM (L16) without a RIFF header while still labeling audio/wav.
// MediaPlayer / AVAudioPlayer need a valid WAV container.
func ensurePlayableWAV(pcmOrWav []byte, rawJSON []byte) []byte {
	if len(pcmOrWav) >= 12 &&
		bytes.Equal(pcmOrWav[0:4], []byte("RIFF")) &&
		bytes.Equal(pcmOrWav[8:12], []byte("WAVE")) {
		return pcmOrWav
	}
	rate := 24000
	if m := rePCMRate.FindSubmatch(rawJSON); len(m) == 2 {
		if n, err := strconv.Atoi(string(m[1])); err == nil && n > 0 {
			rate = n
		}
	}
	return buildWAVPCM16LE(pcmOrWav, rate, 1)
}

func buildWAVPCM16LE(pcm []byte, sampleRate, channels int) []byte {
	if len(pcm) == 0 {
		return pcm
	}
	if len(pcm)%2 != 0 {
		pcm = pcm[:len(pcm)-1]
	}
	const bitsPerSample = 16
	blockAlign := channels * bitsPerSample / 8
	byteRate := sampleRate * blockAlign
	dataSize := len(pcm)
	chunkSize := uint32(36 + dataSize)

	buf := new(bytes.Buffer)
	buf.WriteString("RIFF")
	_ = binary.Write(buf, binary.LittleEndian, chunkSize)
	buf.WriteString("WAVE")
	buf.WriteString("fmt ")
	_ = binary.Write(buf, binary.LittleEndian, uint32(16))
	_ = binary.Write(buf, binary.LittleEndian, uint16(1))
	_ = binary.Write(buf, binary.LittleEndian, uint16(channels))
	_ = binary.Write(buf, binary.LittleEndian, uint32(sampleRate))
	_ = binary.Write(buf, binary.LittleEndian, uint32(byteRate))
	_ = binary.Write(buf, binary.LittleEndian, uint16(blockAlign))
	_ = binary.Write(buf, binary.LittleEndian, uint16(bitsPerSample))
	buf.WriteString("data")
	_ = binary.Write(buf, binary.LittleEndian, uint32(dataSize))
	buf.Write(pcm)
	return buf.Bytes()
}
