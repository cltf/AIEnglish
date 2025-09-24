# 🔧 Gradle网络问题解决指南

## 问题描述
```
Could not install Gradle distribution from 'https://services.gradle.org/distributions/gradle-8.2-bin.zip'.
Reason: java.net.SocketTimeoutException: Read timed out
```

## 🚀 解决方案

### 方案1：使用国内镜像源（推荐）
已配置腾讯云镜像源，速度更快：
```
distributionUrl=https://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip
```

### 方案2：增加超时时间
已将超时时间从10秒增加到60秒：
```
networkTimeout=60000
```

### 方案3：配置Maven镜像源
已添加阿里云Maven镜像源：
```gradle
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/public/' }
        maven { url 'https://maven.aliyun.com/repository/google/' }
        maven { url 'https://maven.aliyun.com/repository/gradle-plugin/' }
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
```

### 方案4：网络配置优化
已添加网络超时配置：
```properties
systemProp.http.connectionTimeout=60000
systemProp.http.socketTimeout=60000
```

### 方案5：手动下载Gradle
如果网络问题持续，可以运行手动下载脚本：
```bash
./download_gradle.sh
```

## 🔄 现在尝试构建

### 清理并重新构建
```bash
# 清理项目
./gradlew clean

# 构建项目
./gradlew build

# 或者直接运行应用
./gradlew assembleDebug
```

### 如果仍有问题
```bash
# 停止Gradle守护进程
./gradlew --stop

# 删除Gradle缓存
rm -rf ~/.gradle/caches

# 重新构建
./gradlew clean build
```

## 🌐 其他镜像源选择

如果腾讯云镜像仍有问题，可以尝试其他镜像：

### 华为云镜像
```properties
distributionUrl=https://mirrors.huaweicloud.com/gradle/gradle-8.2-bin.zip
```

### 阿里云镜像
```properties
distributionUrl=https://mirrors.aliyun.com/macports/distfiles/gradle/gradle-8.2-bin.zip
```

### 网易镜像
```properties
distributionUrl=https://mirrors.163.com/macports/distfiles/gradle/gradle-8.2-bin.zip
```

## 📱 移动网络用户

如果您使用移动网络，建议：
1. 切换到WiFi网络
2. 使用手机热点
3. 尝试不同时间段下载

## 🔧 高级解决方案

### 使用代理
如果您的网络环境需要代理，请在gradle.properties中配置：
```properties
systemProp.http.proxyHost=your-proxy-host
systemProp.http.proxyPort=your-proxy-port
systemProp.https.proxyHost=your-proxy-host
systemProp.https.proxyPort=your-proxy-port
```

### 离线模式
如果网络完全不可用，可以：
1. 下载Gradle到本地
2. 配置本地文件路径
3. 使用离线模式构建

## ✅ 验证安装

构建成功后，您应该看到：
```
BUILD SUCCESSFUL in XXs
```

## 🆘 如果问题持续

1. 检查网络连接
2. 尝试不同的镜像源
3. 联系网络管理员
4. 使用VPN或代理

---

**注意**: 所有配置已经自动应用到项目中，您可以直接尝试构建！





