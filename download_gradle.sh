#!/bin/bash

# Gradle手动下载脚本
# 如果网络问题持续，可以手动下载Gradle

echo "🚀 开始手动下载Gradle..."

# 创建gradle目录
mkdir -p ~/.gradle/wrapper/dists/gradle-8.2-bin

# 下载Gradle
echo "📥 正在下载Gradle 8.2..."
curl -L -o ~/.gradle/wrapper/dists/gradle-8.2-bin/gradle-8.2-bin.zip \
    "https://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip"

if [ $? -eq 0 ]; then
    echo "✅ Gradle下载成功！"
    
    # 解压文件
    echo "📦 正在解压Gradle..."
    cd ~/.gradle/wrapper/dists/gradle-8.2-bin/
    unzip -q gradle-8.2-bin.zip
    
    if [ $? -eq 0 ]; then
        echo "✅ Gradle解压成功！"
        echo "🎉 Gradle安装完成！现在可以运行构建命令了。"
    else
        echo "❌ Gradle解压失败！"
    fi
else
    echo "❌ Gradle下载失败！请检查网络连接。"
fi





