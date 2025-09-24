#!/bin/bash

echo "🔧 修复Gradle权限问题..."

# 设置GRADLE_USER_HOME环境变量
export GRADLE_USER_HOME=$HOME/.gradle
echo "✅ 设置GRADLE_USER_HOME为: $GRADLE_USER_HOME"

# 确保.gradle目录存在并有正确权限
mkdir -p ~/.gradle/wrapper/dists
echo "✅ 创建Gradle目录结构"

# 清理可能存在的损坏的Gradle缓存
echo "🧹 清理Gradle缓存..."
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/wrapper/dists/gradle-7.5-bin/

# 重新创建目录
mkdir -p ~/.gradle/caches
mkdir -p ~/.gradle/wrapper/dists

# 设置正确的权限
chmod -R 755 ~/.gradle
echo "✅ 设置目录权限"

# 验证目录权限
echo "📋 验证目录权限:"
ls -la ~/.gradle/wrapper/dists/

# 测试Gradle wrapper
echo "🧪 测试Gradle wrapper..."
cd /Users/chenlei/Desktop/AIEnglish
./gradlew --version

echo "🎉 Gradle权限修复完成！"
echo "如果仍有问题，请重启Android Studio并重新同步项目。"