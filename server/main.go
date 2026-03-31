// AI 网关本地代理：转发 OpenAI 兼容请求到 ai-svc.xue.xiwang.com，解决浏览器 CORS。
// 运行：cd server && go run .     默认监听 :8787
// 环境变量：PORT（监听端口，默认 8787）、XIWANG_BEARER（Bearer 令牌，必填时可写死在默认值）
package main

import (
	"bytes"
	"io"
	"log"
	"net/http"
	"os"
	"strings"
	"time"
)

const (
	defaultListen = ":8787"
	upstreamURL   = "https://ai-svc.xue.xiwang.com/openai-compatible/v1/chat/completions"
	// 若未设置环境变量 XIWANG_BEARER 则使用（与仓库中原 curl 一致；生产请用环境变量覆盖）
	defaultBearer = "300000454:0e80b963c82d42f842aac3351f21b312"
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
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")
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
