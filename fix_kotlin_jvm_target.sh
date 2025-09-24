#!/bin/bash

echo "🔧 修复Kotlin JVM目标版本问题..."

# 设置Java 17环境变量
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

echo "✅ 设置JAVA_HOME为Java 17: $JAVA_HOME"

# 验证Java版本
echo "📋 当前Java版本："
java -version

# 检查gradle.properties中的JVM参数
echo "📋 检查gradle.properties配置..."
if grep -q "org.gradle.java.home" gradle.properties; then
    echo "✅ gradle.properties中已设置Java 17"
else
    echo "❌ gradle.properties中未设置Java 17"
fi

# 检查app/build.gradle中的JVM目标
echo "📋 检查app/build.gradle配置..."
if grep -q "jvmTarget = '17'" app/build.gradle; then
    echo "✅ app/build.gradle中已设置JVM目标为17"
else
    echo "❌ app/build.gradle中JVM目标不是17"
fi

# 清理所有缓存
echo "🧹 清理所有缓存..."
rm -rf ~/.gradle/caches/
rm -rf .gradle/
rm -rf app/build/

# 测试Gradle配置
echo "🧪 测试Gradle配置..."
./gradlew --version

echo ""
echo "🎉 修复完成！"
echo ""
echo "📋 如果仍然出现'Unknown Kotlin JVM target: 21'错误："
echo "1. 重启Android Studio"
echo "2. 在Android Studio中检查设置："
echo "   File → Settings → Build → Build Tools → Gradle"
echo "   Gradle JVM: 选择 '17 - Eclipse Adoptium 17.0.15+6'"
echo "3. 清理项目：Build → Clean Project"
echo "4. 重新同步：File → Sync Project with Gradle Files"
echo ""
echo "如果问题仍然存在，可能是Android Studio内部配置问题，"
echo "请尝试重新导入项目或重启Android Studio。"




