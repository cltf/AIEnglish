# AIEnglish · iOS

基于仓库根目录 `web/` 的 **WKWebView** 壳应用，能力与浏览器端一致（词库、扫描、识图、阅读分析等）。

## 要求

- Xcode 15+（已用 iOS 16.0+ SDK 验证）
- macOS 上打开：`ios/AIEnglish.xcodeproj`

## 资源说明

工程通过 **文件夹引用** 将 `../web` 打入 App 包；更新前端后重新编译即可。

## AI 代理（识图 / 深度分析）

`app.js` 默认请求 `http://127.0.0.1:8787` 的 Go 代理。使用模拟器时：

1. 在 Mac 上运行：`cd server && go run .`
2. 模拟器内访问宿主机请使用 **`http://127.0.0.1:8787`**（已在 Info.plist 开启 `NSAllowsLocalNetworking`）

真机调试时 `127.0.0.1` 指向手机本身，需将代理部署到局域网可达地址，或后续改为可配置 `EMBEDDED_AI_BASE`。

## 作文离线音频

App 优先使用离线 WAV：`audio/essays/<sample-id>.wav`。

先在仓库根目录批量生成：

```bash
python3 web/scripts/build_essay_audio.py
```

然后重新编译 iOS / Android，离线音频会随资源打包进应用。

## 权限

已声明相机、相册说明文案；首次使用系统会弹窗授权。

## App 图标

与 Android `ic_launcher`（`#3DDC84` 背景 + `ic_launcher_foreground` 矢量）一致。若改 Android 图标，可重新生成：

```bash
python3 ios/scripts/render_app_icon_from_android.py
```

依赖 Pillow（`pip install pillow`）。
