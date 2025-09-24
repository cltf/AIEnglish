# Android Studio 配置指南

## 🎯 目标
配置Android Studio使用正确的Java版本和Gradle版本，解决兼容性问题。

## ✅ 当前状态
- **Gradle版本**: 8.5 ✅
- **Java版本**: 17.0.15 ✅
- **Android API**: 34 ✅
- **Build Tools**: 33.0.1 ✅

## 🔧 Android Studio 配置步骤

### 1. 设置Gradle JVM
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

### 2. 设置项目SDK
1. **进入项目设置**：
   - `File → Project Structure`
2. **设置SDK Location**：
   - 确保 "Android SDK location" 指向：
   - `/Users/chenlei/Library/Android/sdk`
3. **设置JDK Location**：
   - 在 "JDK location" 中选择：
   - `/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home`

### 3. 设置Gradle用户目录
1. **在Gradle设置中**：
   - 确保 "Gradle user home" 设置为：
   - `/Users/chenlei/.gradle`

### 4. 清理和重新同步
1. **清理项目**：
   - `Build → Clean Project`
2. **重新同步**：
   - `File → Sync Project with Gradle Files`

## 🚀 验证配置

### 检查Gradle版本
在Android Studio的Terminal中运行：
```bash
./gradlew --version
```
应该显示：
```
Gradle 8.5
JVM: 17.0.15
```

### 检查Java版本
```bash
java -version
```
应该显示：
```
openjdk version "17.0.15"
```

## 🔍 故障排除

### 如果仍然显示Java 21错误：
1. **重启Android Studio**
2. **检查环境变量**：
   ```bash
   echo $JAVA_HOME
   ```
3. **手动设置环境变量**：
   ```bash
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
   ```

### 如果Gradle同步失败：
1. **清理Gradle缓存**：
   ```bash
   rm -rf ~/.gradle/caches/
   ```
2. **重新同步项目**

## 📋 最终检查清单

- [ ] Android Studio使用Java 17
- [ ] Gradle版本为8.5
- [ ] 项目成功同步
- [ ] 没有兼容性错误
- [ ] 可以正常构建项目

## 🎉 完成

配置完成后，您的项目应该可以正常在Android Studio中工作了！




