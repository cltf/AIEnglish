# ☕ JDK路径配置指南

## 🎯 问题描述
在Android Studio中找不到JDK路径：`/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home`

## ✅ 解决方案

### 方案1：手动输入路径（推荐）

在Android Studio的 `Project Structure` → `SDK Location` 中：

1. **JDK Location字段**，手动输入以下路径之一：

   **选项A（推荐）**：
   ```
   /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
   ```

   **选项B（备选）**：
   ```
   /Library/Java/JavaVirtualMachines/jdk-17.0.1.jdk/Contents/Home
   ```

2. **不要使用浏览按钮**，直接手动输入路径
3. 点击 `Apply` → `OK`

### 方案2：使用系统默认JDK

如果上述路径不工作，可以尝试：

1. **JDK Location字段**输入：
   ```
   /usr/libexec/java_home -v17
   ```

2. 或者使用系统默认路径：
   ```
   /System/Library/Frameworks/JavaVM.framework/Versions/Current/Commands/java
   ```

### 方案3：让Android Studio自动检测

1. 在 `JDK Location` 字段中点击 `...` 浏览按钮
2. 导航到：`/Library/Java/JavaVirtualMachines/`
3. 选择 `temurin-17.jdk` 文件夹
4. 点击 `Select`

### 方案4：使用Android Studio内置JDK

如果所有方法都不行，可以使用Android Studio内置的JDK：

1. 在 `JDK Location` 字段中点击下拉箭头
2. 选择 `Embedded JDK` 或类似选项
3. Android Studio会使用内置的JDK

## 🔍 验证JDK路径

在终端中运行以下命令验证路径：

```bash
# 检查Java 17路径
ls -la /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home

# 验证Java版本
/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home/bin/java -version
```

## 📱 完整的Android Studio配置

### SDK Location设置：
```
Android SDK Location: /Users/chenlei/Library/Android/sdk
JDK Location: /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
```

### 如果JDK路径仍有问题：
1. 尝试使用 `jdk-17.0.1.jdk` 版本
2. 或者让Android Studio使用默认JDK
3. 确保Android Studio版本是最新的

## 🚨 故障排除

### 如果路径仍然无效：
1. **重启Android Studio**
2. **清理项目**：`Build` → `Clean Project`
3. **重新同步**：`File` → `Sync Project with Gradle Files`
4. **检查权限**：确保对Java目录有读取权限

### 替代方案：
如果Java 17配置有问题，可以暂时使用：
- Android Studio内置JDK
- 系统默认Java版本
- 稍后重新配置

## ✅ 配置检查

配置完成后，检查：
- [ ] Android SDK Location 已设置
- [ ] JDK Location 已设置且有效
- [ ] 项目可以正常构建
- [ ] 没有JDK相关错误

---

**记住：手动输入路径通常比使用浏览按钮更可靠！**





