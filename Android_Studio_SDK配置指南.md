# 🔧 Android Studio SDK配置指南

## 🎯 问题描述
错误信息：`Error running '中考词汇'` - `Please select Android SDK`

## ✅ 解决方案

### 步骤1：在Android Studio中配置SDK路径

1. **打开项目设置**：
   - 在Android Studio中，点击 `File` → `Project Structure`
   - 或者使用快捷键 `Cmd + ;`

2. **设置SDK Location**：
   - 在左侧选择 `SDK Location`
   - 设置以下路径：

   ```
   Android SDK Location: /Users/chenlei/Library/Android/sdk
   JDK Location: /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
   ```

3. **应用设置**：
   - 点击 `Apply`
   - 然后点击 `OK`

### 步骤2：安装必要的SDK组件

1. **打开SDK Manager**：
   - 点击 `File` → `Settings`
   - 选择 `Appearance & Behavior` → `System Settings` → `Android SDK`

2. **安装组件**：
   在 `SDK Platforms` 标签页：
   - ✅ 勾选 `Android 13.0 (API 33)`
   
   在 `SDK Tools` 标签页：
   - ✅ 勾选 `Android SDK Build-Tools 33.0.0`
   - ✅ 勾选 `Android SDK Platform-Tools`
   - ✅ 勾选 `Android SDK Tools`

3. **应用更改**：
   - 点击 `Apply`
   - 等待下载和安装完成

### 步骤3：验证配置

配置完成后，尝试运行项目：
- 点击绿色的运行按钮 ▶️
- 或者使用快捷键 `Shift + F10`

## 🚨 如果仍有问题

### 网络连接问题
如果下载SDK组件时遇到网络问题：

1. **配置代理**（如果需要）：
   - 在SDK Manager中点击 `Settings` 齿轮图标
   - 配置HTTP代理设置

2. **使用国内镜像**：
   - 在 `gradle.properties` 中已配置阿里云镜像
   - SDK下载可能需要VPN或代理

### 替代方案
如果无法解决SDK问题，可以：

1. **使用模拟器**：
   - 创建Android虚拟设备 (AVD)
   - 在模拟器中运行应用

2. **使用真机调试**：
   - 连接Android手机
   - 启用USB调试模式
   - 在真机上运行应用

## 📱 项目信息

- **应用名称**: 中考词汇扫描助手
- **包名**: com.vocabulary.scanner
- **目标SDK**: Android 13 (API 33)
- **最小SDK**: Android 7.0 (API 24)

## ✅ 配置检查清单

- [ ] Android SDK路径已设置
- [ ] JDK路径已设置
- [ ] Android 13.0 (API 33) 已安装
- [ ] Build-Tools 33.0.0 已安装
- [ ] Platform-Tools 已安装
- [ ] 项目可以正常构建

## 🎉 预期结果

配置完成后，您应该能够：
1. ✅ 成功构建项目
2. ✅ 在模拟器或真机上运行应用
3. ✅ 看到"中考词汇扫描助手"的主界面
4. ✅ 使用词汇扫描和查询功能

---

**配置完成后，您的应用就可以正常运行了！** 🚀





