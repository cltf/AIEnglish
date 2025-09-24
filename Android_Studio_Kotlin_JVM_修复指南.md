# Android Studio Kotlin JVM目标版本修复指南

## 🔍 问题描述
```
Unknown Kotlin JVM target: 21
```

## 🎯 问题原因
这个错误通常出现在以下情况：
1. Android Studio内部配置与项目配置不一致
2. Android Studio缓存了旧的配置
3. Android Studio的Gradle JVM设置不正确

## ✅ 解决方案

### 1. 验证项目配置（已完成）
- ✅ `build.gradle`: Kotlin 1.8.22
- ✅ `app/build.gradle`: JVM目标为17
- ✅ `gradle.properties`: Java 17配置
- ✅ 终端Gradle: 使用Java 17

### 2. Android Studio配置步骤

#### 步骤1: 设置Gradle JVM
1. **打开Android Studio**
2. **进入设置**：
   - macOS: `Android Studio → Preferences`
   - Windows/Linux: `File → Settings`
3. **导航到Gradle设置**：
   - `Build, Execution, Deployment → Build Tools → Gradle`
4. **设置Gradle JVM**：
   - 在 "Gradle JVM" 下拉菜单中选择：
   - `17 - Eclipse Adoptium 17.0.15+6`
   - 或者选择：`/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home`

#### 步骤2: 清理项目
1. **清理项目**：
   - `Build → Clean Project`
2. **清理缓存**：
   - `File → Invalidate Caches and Restart...`
   - 选择 "Invalidate and Restart"

#### 步骤3: 重新同步
1. **重新同步项目**：
   - `File → Sync Project with Gradle Files`

### 3. 如果问题仍然存在

#### 方案A: 重新导入项目
1. **关闭Android Studio**
2. **删除项目缓存**：
   ```bash
   rm -rf .idea/
   rm -rf app/build/
   rm -rf .gradle/
   ```
3. **重新打开项目**

#### 方案B: 检查项目结构
1. **打开项目结构**：
   - `File → Project Structure`
2. **检查SDK设置**：
   - 确保 "Project SDK" 设置为正确的Android SDK
   - 确保 "Project language level" 设置为合适的版本

#### 方案C: 手动设置环境变量
在Android Studio的Terminal中运行：
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
./gradlew clean
./gradlew build
```

### 4. 验证修复
运行以下命令验证配置：
```bash
./gradlew --version
```
应该显示：
```
Gradle 8.5
JVM: 17.0.15
```

## 🚨 常见问题

### Q: 为什么终端显示Kotlin 1.9.20？
A: 这可能是Gradle缓存问题。运行 `./gradlew clean` 清理缓存。

### Q: Android Studio仍然显示错误？
A: 确保Android Studio的Gradle JVM设置正确，并重启Android Studio。

### Q: 项目无法同步？
A: 检查网络连接，确保可以访问Gradle仓库。

## 📋 检查清单

- [ ] Android Studio使用Java 17
- [ ] 项目配置使用Java 17
- [ ] Gradle JVM设置为Java 17
- [ ] 项目成功同步
- [ ] 没有Kotlin JVM目标错误

## 🎉 完成

按照以上步骤操作后，Kotlin JVM目标版本问题应该得到解决！




