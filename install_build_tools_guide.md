# 安装Android Build Tools 30.0.3 指南

## 问题描述
您的项目需要Build Tools 30.0.3，但系统中没有安装这个版本。

## 解决方案

### 方法1：通过Android Studio安装（推荐）

1. **打开Android Studio**
2. **进入SDK设置**：
   - File → Settings（Windows/Linux）
   - Android Studio → Preferences（macOS）
3. **导航到Android SDK**：
   - Appearance & Behavior → System Settings → Android SDK
4. **切换到SDK Tools标签**
5. **安装命令行工具**（如果还没有）：
   - 勾选 "Android SDK Command-line Tools (latest)"
   - 点击 "Apply" 安装
6. **安装Build Tools**：
   - 在 "SDK Tools" 标签中
   - 勾选 "Android SDK Build-Tools 30.0.3"
   - 点击 "Apply" 安装

### 方法2：手动下载安装

如果方法1不工作，可以尝试：

1. **下载Build Tools 30.0.3**：
   - 访问：https://developer.android.com/studio/releases/build-tools
   - 下载 build-tools_r30.0.3-macos.zip

2. **解压到正确位置**：
   ```bash
   mkdir -p ~/Library/Android/sdk/build-tools/30.0.3
   # 解压下载的文件到这个目录
   ```

### 方法3：更新项目配置

如果无法安装30.0.3，可以更新项目使用已有的Build Tools版本：

1. **检查可用的Build Tools版本**：
   ```bash
   ls -la ~/Library/Android/sdk/build-tools/
   ```

2. **更新app/build.gradle**：
   ```gradle
   android {
       buildToolsVersion "33.0.1"  // 使用您已有的版本
       // 其他配置...
   }
   ```

## 验证安装

安装完成后，验证Build Tools是否正确安装：

```bash
ls -la ~/Library/Android/sdk/build-tools/30.0.3
```

## 下一步

1. 重启Android Studio
2. File → Sync Project with Gradle Files
3. 尝试构建项目

## 常见问题

**Q: 为什么需要Build Tools 30.0.3？**
A: 您的项目配置中指定了这个版本，或者某个依赖库需要这个版本。

**Q: 可以使用其他版本吗？**
A: 可以，但建议使用项目要求的版本以避免兼容性问题。

**Q: 安装后仍然报错怎么办？**
A: 清理项目缓存：Build → Clean Project，然后重新同步。




