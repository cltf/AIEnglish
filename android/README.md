# AIEnglish Android（Jetpack Compose）

与 iOS / Web 对齐的「中考词汇助手」：**词库**、**扫描识图（本地代理 OCR）**、**阅读分析**、**生词本**、**词典例句**。

## 环境

- Android Studio Hedgehog+，JDK 17
- 在 `android/` 下创建 `local.properties`（勿提交仓库）：

```properties
sdk.dir=/path/to/Android/sdk
```

也可复制仓库根目录的 `local.properties` 到本目录。

## 构建

```bash
cd android
./gradlew :app:assembleDebug
```

构建前会将仓库根目录的 `web/data/vocabulary.json` 复制到 `app/src/main/assets/`（若文件存在）。

## 本地 AI 代理（OCR / 深度分析）

默认通过 **`BuildConfig.AI_PROXY_HOST`** 访问 `http://<host>:8787`（与 iOS 一致路径）。

- **Android 模拟器**：默认主机为 **`10.0.2.2`**（映射到你电脑上的 127.0.0.1），请在电脑运行 Go 代理监听 `8787`。
- **实体机 + 代理跑在手机上**：将 `app/build.gradle` 中 `buildConfigField "String", "AI_PROXY_HOST", "\"10.0.2.2\""` 改为 **`\"127.0.0.1\"`** 后重新编译。

`network_security_config` 已允许明文访问本机回环地址。

## 与仓库根目录 `app/` 模块的关系

历史工程仍保留在仓库根目录的 `:app`（`com.vocabulary.scanner`）。本目录为 **独立 Gradle 工程**（`com.cltf.aienglish`），与 iOS bundle id 对齐，可按需逐步迁移或并存。
