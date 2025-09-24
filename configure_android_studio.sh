#!/bin/bash

echo "🔧 配置Android Studio和Gradle..."

# 设置Java 17环境变量
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

echo "✅ 设置JAVA_HOME为Java 17: $JAVA_HOME"

# 验证Java版本
echo "📋 当前Java版本："
java -version

# 验证Gradle版本
echo "📋 当前Gradle版本："
./gradlew --version

# 清理Gradle缓存
echo "🧹 清理Gradle缓存..."
rm -rf ~/.gradle/caches/

# 创建gradle.properties文件确保使用Java 17
echo "📝 更新gradle.properties..."
cat >> gradle.properties << 'EOF'

# 强制使用Java 17
org.gradle.java.home=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home

# 抑制API 34警告
android.suppressUnsupportedCompileSdk=34
EOF

echo "✅ gradle.properties已更新"

# 测试项目构建
echo "🧪 测试项目构建..."
./gradlew clean

echo ""
echo "🎉 配置完成！"
echo ""
echo "📋 下一步操作："
echo "1. 重启Android Studio"
echo "2. 在Android Studio中设置Gradle JVM为Java 17："
echo "   File → Settings → Build → Build Tools → Gradle"
echo "   Gradle JVM: 选择 '17 - Eclipse Adoptium 17.0.15+6'"
echo "3. File → Sync Project with Gradle Files"
echo ""
echo "如果还有问题，请查看 'Android_Studio_配置指南.md' 文件。"




