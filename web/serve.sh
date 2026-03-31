#!/usr/bin/env bash
# 本地静态服务：浏览器打开 http://127.0.0.1:${PORT:-8080}/index.html
set -euo pipefail
cd "$(dirname "$0")"
PORT="${PORT:-8765}"
echo "Serving web/ at http://127.0.0.1:${PORT}/index.html (Ctrl+C 停止)"
echo "若使用识图/AI：另开终端执行  cd server && go run .  （代理 :8787）"
exec python3 -m http.server "$PORT"
