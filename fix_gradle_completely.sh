#!/bin/bash

echo "🔧 彻底修复Gradle配置问题..."

# 设置正确的环境变量
export GRADLE_USER_HOME="$HOME/.gradle"
export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"

echo "📂 Gradle用户目录: $GRADLE_USER_HOME"
echo "☕ Java路径: $JAVA_HOME"

# 1. 停止所有Gradle守护进程
echo "🛑 停止所有Gradle守护进程..."
./gradlew --stop 2>/dev/null || true

# 2. 清理所有Gradle缓存和临时文件
echo "🧹 清理所有Gradle缓存..."
rm -rf "$GRADLE_USER_HOME/caches"
rm -rf "$GRADLE_USER_HOME/daemon"
rm -rf "$GRADLE_USER_HOME/wrapper"
rm -rf "$GRADLE_USER_HOME/.tmp"

# 3. 重新创建Gradle目录结构
echo "📁 重新创建Gradle目录结构..."
mkdir -p "$GRADLE_USER_HOME/caches"
mkdir -p "$GRADLE_USER_HOME/daemon"
mkdir -p "$GRADLE_USER_HOME/wrapper/dists"
mkdir -p "$GRADLE_USER_HOME/.tmp"

# 4. 设置正确的权限
echo "🔐 设置目录权限..."
chmod -R 755 "$GRADLE_USER_HOME"

# 5. 创建gradle.properties文件
echo "📝 创建gradle.properties配置..."
cat > gradle.properties << 'EOF'
# Project-wide Gradle settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# 网络配置
systemProp.http.proxyHost=
systemProp.http.proxyPort=
systemProp.https.proxyHost=
systemProp.https.proxyPort=

# 超时配置
systemProp.http.connectionTimeout=60000
systemProp.http.socketTimeout=60000

# AndroidX package structure
android.useAndroidX=true

# Kotlin code style
kotlin.code.style=official

# Enable namespacing
android.nonTransitiveRClass=true

# Gradle配置
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true

# 强制使用用户目录
org.gradle.user.home=/Users/chenlei/.gradle
EOF

# 6. 验证gradle-wrapper.properties
echo "🔍 检查gradle-wrapper.properties..."
if [ -f "gradle/wrapper/gradle-wrapper.properties" ]; then
    echo "✅ gradle-wrapper.properties 存在"
    cat gradle/wrapper/gradle-wrapper.properties
else
    echo "❌ gradle-wrapper.properties 不存在"
fi

# 7. 验证gradle-wrapper.jar
echo "🔍 检查gradle-wrapper.jar..."
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "✅ gradle-wrapper.jar 存在 ($(stat -f%z gradle/wrapper/gradle-wrapper.jar) 字节)"
else
    echo "❌ gradle-wrapper.jar 不存在"
fi

# 8. 设置环境变量并尝试构建
echo "🚀 尝试构建项目..."
export GRADLE_USER_HOME="$HOME/.gradle"
export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "环境变量:"
echo "GRADLE_USER_HOME=$GRADLE_USER_HOME"
echo "JAVA_HOME=$JAVA_HOME"

# 尝试构建
./gradlew clean --info





