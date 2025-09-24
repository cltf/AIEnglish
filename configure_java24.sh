#!/bin/bash

echo "🔧 配置项目使用Java 24..."

# 设置Java 24环境变量
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

echo "✅ 设置JAVA_HOME为Java 24: $JAVA_HOME"

# 验证Java版本
echo "📋 当前Java版本："
java -version

# 更新gradle.properties
echo "📝 更新gradle.properties..."
cat > gradle.properties << 'EOF'
# Project-wide Gradle settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# 强制设置Gradle用户目录
org.gradle.user.home=/Users/chenlei/.gradle

# 强制使用Java 24
org.gradle.java.home=/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home

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

# 更新build.gradle
echo "📝 更新build.gradle..."
cat > build.gradle << 'EOF'
// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id 'com.android.application' version '8.5.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.24' apply false
    id 'org.jetbrains.kotlin.kapt' version '1.9.24' apply false
}

// 仓库配置已移至settings.gradle

task clean(type: Delete) {
    delete rootProject.buildDir
}
EOF

# 更新app/build.gradle中的Java版本
echo "📝 更新app/build.gradle..."
sed -i '' 's/JavaVersion.VERSION_17/JavaVersion.VERSION_21/g' app/build.gradle
sed -i '' "s/jvmTarget = '17'/jvmTarget = '21'/g" app/build.gradle

# 清理缓存
echo "🧹 清理缓存..."
rm -rf ~/.gradle/caches/
rm -rf .gradle/
rm -rf app/build/

# 测试Gradle
echo "🧪 测试Gradle..."
./gradlew --version

echo ""
echo "🎉 Java 24配置完成！"
echo ""
echo "📋 下一步："
echo "1. 重启Android Studio"
echo "2. 在Android Studio中设置Gradle JVM为Java 24"
echo "3. File → Sync Project with Gradle Files"




