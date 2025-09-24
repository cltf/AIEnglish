#!/bin/bash

echo "🔧 修复Java和Gradle兼容性问题..."

# 检查可用的Java版本
echo "📋 检查可用的Java版本："
/usr/libexec/java_home -V

# 查找Java 17
JAVA17_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null)
if [ -n "$JAVA17_HOME" ]; then
    echo "✅ 找到Java 17: $JAVA17_HOME"
    export JAVA_HOME="$JAVA17_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "✅ 设置JAVA_HOME为Java 17"
else
    echo "❌ 未找到Java 17"
    echo "请安装Java 17："
    echo "1. 访问：https://adoptium.net/"
    echo "2. 下载并安装Java 17"
    echo "3. 重新运行此脚本"
    exit 1
fi

# 验证Java版本
echo "📋 当前Java版本："
java -version

# 清理Gradle缓存
echo "🧹 清理Gradle缓存..."
rm -rf ~/.gradle/caches/
rm -rf gradle/wrapper/dists/

# 使用Java 17运行Gradle
echo "🧪 使用Java 17测试Gradle..."
./gradlew --version

echo ""
echo "✅ 修复完成！"
echo "现在可以使用Java 17运行Gradle了。"
echo ""
echo "📋 下一步："
echo "1. 重启Android Studio"
echo "2. 在Android Studio中设置Java 17："
echo "   File → Settings → Build → Build Tools → Gradle"
echo "   Gradle JVM: 选择Java 17"
echo "3. File → Sync Project with Gradle Files"




