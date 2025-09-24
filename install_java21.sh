#!/bin/bash

echo "🔧 安装Java 21..."

# 检查是否已安装Java 21
if [ -d "/Library/Java/JavaVirtualMachines/temurin-21.jdk" ]; then
    echo "✅ Java 21 已安装"
    exit 0
fi

echo "📥 下载Java 21..."
# 使用Homebrew安装Java 21
if command -v brew &> /dev/null; then
    echo "使用Homebrew安装Java 21..."
    brew install --cask temurin21
else
    echo "❌ 未找到Homebrew，请手动安装Java 21"
    echo "访问：https://adoptium.net/temurin/releases/?version=21"
    echo "下载并安装Java 21"
    exit 1
fi

echo "✅ Java 21 安装完成！"
echo "📋 验证安装："
/usr/libexec/java_home -v 21




