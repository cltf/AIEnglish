# Android Studio Gradle JDK 配置指南

## 🔍 问题描述
```
Invalid Gradle JDK configuration found.
Use Embedded JDK (/Applications/Android Studio.app/Contents/jbr/Contents/Home)
Change Gradle JDK location
```

## 🎯 问题原因
Android Studio检测到Gradle JDK配置无效，建议使用嵌入式JDK。

## ✅ 解决方案

### 方案1：使用Android Studio嵌入式JDK（推荐）

#### 步骤1：打开Gradle设置
1. **打开Android Studio**
2. **进入设置**：
   - macOS: `Android Studio → Preferences`
   - Windows/Linux: `File → Settings`
3. **导航到Gradle设置**：
   - `Build, Execution, Deployment → Build Tools → Gradle`

#### 步骤2：配置Gradle JDK
1. **在"Gradle JVM"下拉菜单中选择**：
   - `Embedded JDK` 或
   - `/Applications/Android Studio.app/Contents/jbr/Contents/Home`

#### 步骤3：应用设置
1. **点击"Apply"**
2. **点击"OK"**

### 方案2：使用系统Java 21

如果您想使用系统的Java 21，可以选择：
- `21 - Eclipse Adoptium 21.0.7+9`（如果可用）

### 方案3：使用Java 24（当前项目配置）

如果您想继续使用Java 24：
- `24 - Eclipse Adoptium 24.0.1+9`

## 📋 当前可用的JDK选项

### Android Studio嵌入式JDK
- **路径**: `/Applications/Android Studio.app/Contents/jbr/Contents/Home`
- **版本**: Java 21.0.7
- **状态**: ✅ 推荐使用

### 系统JDK
- **Java 24**: `/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home`
- **Java 17**: `/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home`

## 🔧 推荐配置

### 最佳实践
1. **使用Android Studio嵌入式JDK**（Java 21）
2. **更新项目配置以匹配**：
   ```gradle
   // app/build.gradle
   compileOptions {
       sourceCompatibility JavaVersion.VERSION_21
       targetCompatibility JavaVersion.VERSION_21
   }
   
   kotlinOptions {
       jvmTarget = '21'
   }
   ```

### 更新项目配置
如果您选择使用嵌入式JDK，需要更新项目配置：

1. **更新gradle.properties**：
   ```properties
   # 使用Android Studio嵌入式JDK
   org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home
   ```

2. **更新app/build.gradle**：
   ```gradle
   compileOptions {
       sourceCompatibility JavaVersion.VERSION_21
       targetCompatibility JavaVersion.VERSION_21
   }
   
   kotlinOptions {
       jvmTarget = '21'
   }
   ```

## 🚀 操作步骤

### 立即操作
1. **在Android Studio中**：
   - File → Settings → Build → Build Tools → Gradle
   - Gradle JVM: 选择 "Embedded JDK"
   - 点击 "Apply" 和 "OK"

2. **同步项目**：
   - File → Sync Project with Gradle Files

### 如果选择更新项目配置
运行以下脚本自动更新：
```bash
./configure_embedded_jdk.sh
```

## 🎉 完成

配置完成后，您的项目应该可以正常同步了！

## 📝 注意事项

- Android Studio嵌入式JDK是经过测试的稳定版本
- 使用嵌入式JDK可以避免版本兼容性问题
- 如果遇到问题，可以尝试清理缓存：Build → Clean Project




