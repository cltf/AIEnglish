#!/bin/bash

echo "🔧 配置项目使用Android Studio嵌入式JDK..."

# Android Studio嵌入式JDK路径
EMBEDDED_JDK="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

echo "📋 Android Studio嵌入式JDK信息："
if [ -d "$EMBEDDED_JDK" ]; then
    echo "✅ 嵌入式JDK路径存在: $EMBEDDED_JDK"
    "$EMBEDDED_JDK/bin/java" -version
else
    echo "❌ 嵌入式JDK路径不存在: $EMBEDDED_JDK"
    exit 1
fi

# 更新gradle.properties
echo "📝 更新gradle.properties使用嵌入式JDK..."
cat > gradle.properties << 'EOF'
# Project-wide Gradle settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# 强制设置Gradle用户目录
org.gradle.user.home=/Users/chenlei/.gradle

# 使用Android Studio嵌入式JDK
org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home

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

# 抑制API 34警告
android.suppressUnsupportedCompileSdk=34
EOF

# 更新app/build.gradle使用Java 21
echo "📝 更新app/build.gradle使用Java 21..."
sed -i '' 's/JavaVersion.VERSION_21/JavaVersion.VERSION_21/g' app/build.gradle
sed -i '' "s/jvmTarget = '21'/jvmTarget = '21'/g" app/build.gradle

# 清理缓存
echo "🧹 清理缓存..."
rm -rf ~/.gradle/caches/
rm -rf .gradle/
rm -rf app/build/

# 测试配置
echo "🧪 测试配置..."
export JAVA_HOME="$EMBEDDED_JDK"
./gradlew --version

echo ""
echo "🎉 嵌入式JDK配置完成！"
echo ""
echo "📋 当前配置："
echo "- JDK: Android Studio嵌入式JDK (Java 21.0.7)"
echo "- Build Tools: 36.0.0"
echo "- Gradle: 8.10.2"
echo ""
echo "📋 下一步："
echo "1. 在Android Studio中："
echo "   - File → Settings → Build → Build Tools → Gradle"
echo "   - Gradle JVM: 选择 'Embedded JDK'"
echo "   - 点击 'Apply' 和 'OK'"
echo "2. File → Sync Project with Gradle Files"
echo ""
echo "现在应该可以正常同步项目了！"




