#!/bin/bash

echo "🔧 修复Build Tools版本问题..."

# 设置Java 24环境变量
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-24.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

echo "✅ 设置JAVA_HOME为Java 24: $JAVA_HOME"

# 检查可用的Build Tools版本
echo "📋 可用的Build Tools版本："
ls -la ~/Library/Android/sdk/build-tools/

# 确保app/build.gradle使用正确的Build Tools版本
echo "📝 更新app/build.gradle使用Build Tools 33.0.1..."
sed -i '' 's/buildToolsVersion "34.0.0"/buildToolsVersion "33.0.1"/g' app/build.gradle

# 或者使用36.0.0（更新的版本）
echo "📝 或者使用Build Tools 36.0.0..."
sed -i '' 's/buildToolsVersion "33.0.1"/buildToolsVersion "36.0.0"/g' app/build.gradle

# 清理所有缓存
echo "🧹 清理所有缓存..."
rm -rf ~/.gradle/caches/
rm -rf .gradle/
rm -rf app/build/
rm -rf .idea/

# 重新创建.idea目录（Android Studio项目配置）
echo "📁 重新创建项目配置目录..."
mkdir -p .idea

# 测试Gradle配置
echo "🧪 测试Gradle配置..."
./gradlew --version

echo ""
echo "🎉 Build Tools修复完成！"
echo ""
echo "📋 当前配置："
echo "- Build Tools版本: 36.0.0"
echo "- Java版本: 24.0.1"
echo "- Gradle版本: 8.10.2"
echo ""
echo "📋 下一步："
echo "1. 重启Android Studio"
echo "2. 在Android Studio中："
echo "   - File → Sync Project with Gradle Files"
echo "   - 如果还有问题，尝试："
echo "     - Build → Clean Project"
echo "     - File → Invalidate Caches and Restart..."




