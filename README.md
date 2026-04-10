# 中考应试

面向北京中考备考的 **全科学习与查阅** 应用：在手机上集中查看 **语文、数学、英语、物理、道德与法治** 等科目的试卷结构说明与真题类内容，并配套英语词库、阅读与作文等能力。本仓库包含 **Android（Jetpack Compose）**、**iOS（SwiftUI）** 与 **Web 前端**，数据与 JSON 资源在 `web/data` 与各端 assets 间对齐。

---

## 应用定位

- **名称**：中考应试（各端显示名一致；iOS Bundle 工程名为 AIZhongkao）
- **场景**：考前系统查阅试卷结构、浏览历年/中考真题条目、英语词汇与阅读辅助
- **说明**：题目与解析以备考参考为主，内容持续校对，请以应用内展示为准

---

## 功能概览（按底部 Tab）

| Tab | 内容要点 |
|-----|----------|
| **语文** | 试卷结构、作文、中考真题（分区浏览） |
| **数学** | 试卷结构、历年真题（条目列表与试卷图） |
| **英语** | 试卷结构、词库、英语阅读（识图/文本）、阅读高频、阅读技巧、21 天 688 词、英语作文等 |
| **物理** | 试卷结构、中考真题 |
| **道法** | 试卷结构、主观题、历年中考题等 |
| **我的** | 字体等设置；英语生词本、扫描相关能力与模型配置（因端而异） |

英语模块中的 **拍照/相册识图、AI 深度分析** 等可能依赖网络或本地代理，请以各端「我的」或应用内说明为准。

---

## 仓库结构

```
├── android/          # Android 应用（Kotlin + Jetpack Compose），模块 app
├── ios/              # iOS 应用（SwiftUI），打开 ios/AIZhongkao.xcodeproj
├── web/              # Web 前端（词库、面板与脚本；部分数据与 App 共用）
├── server/           # Go 代理服务（可选，用于本地 AI/OCR 等调试）
└── web/data/         # 各科 JSON、试卷图片等资源的「源」之一，iOS 工程通过引用参与打包
```

---

## Android

- **打开方式**：Android Studio 打开仓库根目录下的 `android/`（或根目录若配置为 Gradle 工程则按你本地习惯）
- **主要技术**：Kotlin、Jetpack Compose、Material 3
- **权限**：相机、相册等用于英语扫描识图（见 `AndroidManifest` 与系统授权说明）
- **本地配置**：可在 `android/local.properties` 中配置 `sdk.dir` 等

构建与依赖以 `android/app/build.gradle`、`android/settings.gradle` 为准。

---

## iOS

- **打开方式**：Xcode 打开 `ios/AIZhongkao.xcodeproj`
- **系统要求**：Xcode 15+，部署目标以工程设置为准（如 iOS 16+）
- **启动体验**：系统启动页与 SwiftUI 闪屏（圆形 Logo + 标语）衔接；`Info.plist` 中 `UILaunchScreen` 与资源在 `Assets.xcassets`
- **网络**：如需访问本机代理，已配置允许本地网络访问（见 `Info.plist`）；真机调试代理地址需自行改为可访问的主机

---

## Web

- 静态页面与脚本位于 `web/`，可通过本地 HTTP 服务器打开 `web/index.html` 调试
- 与 App 共享部分 `web/data/*.json` 与图片目录；更新数据后需同步各端打包资源

---

## 数据与资源

- 各科 **试卷结构** 多为 `*_beijing_structure.json`；**真题条目** 多为 `*_zhongkao.json` 等
- **数学 / 物理 / 语文** 等真题配图位于 `web/data` 下对应年份或科目目录，Android 侧有 `android/app/src/main/assets` 副本以便离线
- 修改 JSON 或图片后，请同时更新 **Web** 与 **Android/iOS** 中引用路径一致，避免一端遗漏

---

## 可选：本地 Go 代理（server）

用于开发时转发 AI/OCR 等请求（详见 `server/main.go`）。在仓库 `server` 目录执行 `go run .` 即可启动；**不要将编译生成的 `server/proxy` 二进制提交到 Git**。

---

## 贡献与规范

欢迎通过 Issue / Pull Request 反馈问题或提交改进。提交前请保持各端行为与文案在能力范围内一致，并注意大体积图片与二进制不要误入版本库。

---

## 许可证

本项目采用 MIT 许可证；详见仓库内 [LICENSE](LICENSE)（若存在）。

---

**中考应试** — 抓紧时间，掌握技巧，备考更高效。
