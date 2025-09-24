#!/bin/bash

# 中考词汇扫描助手 - 编译脚本
echo "🚀 开始编译中考词汇扫描助手..."

# 检查Java环境
echo "📋 检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java，请安装JDK 8或更高版本"
    exit 1
fi

java -version

# 检查Android SDK
echo "📋 检查Android SDK..."
if [ ! -d "$ANDROID_HOME" ] && [ ! -d "$ANDROID_SDK_ROOT" ]; then
    echo "⚠️  警告: 未设置ANDROID_HOME环境变量"
    echo "请设置Android SDK路径，例如："
    echo "export ANDROID_HOME=/Users/chenlei/Library/Android/sdk"
fi

# 检查项目文件
echo "📋 检查项目文件..."
if [ ! -f "build.gradle" ]; then
    echo "❌ 错误: 未找到build.gradle文件"
    exit 1
fi

if [ ! -f "app/build.gradle" ]; then
    echo "❌ 错误: 未找到app/build.gradle文件"
    exit 1
fi

echo "✅ 项目文件检查完成"

# 创建Gradle Wrapper（如果需要）
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "📦 下载Gradle Wrapper..."
    # 这里需要手动下载gradle-wrapper.jar文件
    echo "⚠️  请手动下载gradle-wrapper.jar文件到 gradle/wrapper/ 目录"
    echo "下载地址: https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar"
fi

# 尝试编译
echo "🔨 开始编译项目..."

# 使用Android Studio的Gradle命令
if command -v gradle &> /dev/null; then
    echo "使用系统Gradle..."
    gradle clean assembleDebug
elif [ -f "gradlew" ]; then
    echo "使用Gradle Wrapper..."
    chmod +x gradlew
    ./gradlew clean assembleDebug
else
    echo "❌ 错误: 未找到Gradle"
    echo "请安装Gradle或使用Android Studio编译"
    exit 1
fi

# 检查编译结果
if [ $? -eq 0 ]; then
    echo "✅ 编译成功！"
    echo "📱 APK文件位置: app/build/outputs/apk/debug/app-debug.apk"
    echo "🎉 您可以使用Android Studio安装到设备或模拟器"
else
    echo "❌ 编译失败"
    echo "请检查错误信息并修复问题"
    exit 1
fi

echo "📚 更多信息请查看 编译指南.md"





