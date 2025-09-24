#!/bin/bash

echo "🔧 安装Gradle Wrapper..."

# 创建gradle wrapper目录
mkdir -p gradle/wrapper

# 下载gradle-wrapper.jar（使用国内镜像）
echo "📥 下载gradle-wrapper.jar..."

# 尝试多个镜像源
MIRRORS=(
    "https://mirrors.cloud.tencent.com/gradle/gradle-wrapper.jar"
    "https://mirrors.huaweicloud.com/gradle/gradle-wrapper.jar"
    "https://services.gradle.org/distributions/gradle-wrapper.jar"
)

for mirror in "${MIRRORS[@]}"; do
    echo "尝试从 $mirror 下载..."
    if curl -L --connect-timeout 30 -o gradle/wrapper/gradle-wrapper.jar "$mirror"; then
        echo "✅ 下载成功！"
        break
    else
        echo "❌ 下载失败，尝试下一个镜像..."
    fi
done

# 检查文件是否存在
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "✅ gradle-wrapper.jar 安装成功！"
    
    # 设置权限
    chmod +x gradlew
    chmod +x gradlew.bat
    
    echo "🎉 Gradle Wrapper 安装完成！"
    echo "现在可以运行: ./gradlew --version"
else
    echo "❌ 无法下载gradle-wrapper.jar"
    echo "请手动下载并放置到 gradle/wrapper/ 目录下"
fi





