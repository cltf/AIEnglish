#!/bin/bash

echo "🔧 修复Gradle Wrapper..."

# 删除错误的文件
rm -f gradle/wrapper/gradle-wrapper.jar

echo "📥 从GitHub下载正确的gradle-wrapper.jar..."

# 尝试多个下载源
URLS=(
    "https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar"
    "https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar"
    "https://repo1.maven.org/maven2/org/gradle/gradle-wrapper/8.2/gradle-wrapper-8.2.jar"
)

for url in "${URLS[@]}"; do
    echo "尝试从 $url 下载..."
    if curl -L --connect-timeout 30 -o gradle/wrapper/gradle-wrapper.jar "$url"; then
        # 检查文件大小
        size=$(stat -f%z gradle/wrapper/gradle-wrapper.jar 2>/dev/null || echo "0")
        if [ "$size" -gt 50000 ]; then
            echo "✅ 下载成功！文件大小: $size 字节"
            break
        else
            echo "❌ 文件太小，可能不是正确的JAR文件"
            rm -f gradle/wrapper/gradle-wrapper.jar
        fi
    else
        echo "❌ 下载失败"
    fi
done

# 检查最终结果
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    size=$(stat -f%z gradle/wrapper/gradle-wrapper.jar 2>/dev/null || echo "0")
    if [ "$size" -gt 50000 ]; then
        echo "✅ gradle-wrapper.jar 安装成功！"
        echo "🎉 现在可以运行: ./gradlew --version"
    else
        echo "❌ gradle-wrapper.jar 文件有问题"
        echo "💡 建议使用Android Studio打开项目"
    fi
else
    echo "❌ 无法下载gradle-wrapper.jar"
    echo "💡 建议使用Android Studio打开项目"
fi





