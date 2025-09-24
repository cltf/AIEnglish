#!/bin/bash

echo "🔧 安装Android Build Tools 30.0.3..."

# 设置Android SDK路径
ANDROID_SDK="$HOME/Library/Android/sdk"
echo "📁 Android SDK路径: $ANDROID_SDK"

# 检查Android SDK是否存在
if [ ! -d "$ANDROID_SDK" ]; then
    echo "❌ Android SDK目录不存在: $ANDROID_SDK"
    echo "请先安装Android Studio并设置SDK路径"
    exit 1
fi

# 查找sdkmanager
SDKMANAGER=""
if [ -f "$ANDROID_SDK/cmdline-tools/latest/bin/sdkmanager" ]; then
    SDKMANAGER="$ANDROID_SDK/cmdline-tools/latest/bin/sdkmanager"
elif [ -f "$ANDROID_SDK/tools/bin/sdkmanager" ]; then
    SDKMANAGER="$ANDROID_SDK/tools/bin/sdkmanager"
elif [ -f "$ANDROID_SDK/platform-tools/sdkmanager" ]; then
    SDKMANAGER="$ANDROID_SDK/platform-tools/sdkmanager"
else
    echo "❌ 找不到sdkmanager"
    echo "尝试通过Android Studio安装命令行工具..."
    echo "1. 打开Android Studio"
    echo "2. File → Settings → Appearance & Behavior → System Settings → Android SDK"
    echo "3. 切换到 'SDK Tools' 标签"
    echo "4. 勾选 'Android SDK Command-line Tools (latest)'"
    echo "5. 点击 'Apply' 安装"
    exit 1
fi

echo "✅ 找到sdkmanager: $SDKMANAGER"

# 设置环境变量
export ANDROID_HOME="$ANDROID_SDK"
export PATH="$PATH:$ANDROID_SDK/cmdline-tools/latest/bin:$ANDROID_SDK/platform-tools"

# 接受许可证
echo "📋 接受Android SDK许可证..."
yes | "$SDKMANAGER" --licenses

# 安装Build Tools 30.0.3
echo "📦 安装Build Tools 30.0.3..."
"$SDKMANAGER" "build-tools;30.0.3"

# 验证安装
echo "✅ 验证安装..."
if [ -d "$ANDROID_SDK/build-tools/30.0.3" ]; then
    echo "🎉 Build Tools 30.0.3 安装成功！"
    ls -la "$ANDROID_SDK/build-tools/30.0.3"
else
    echo "❌ Build Tools 30.0.3 安装失败"
    echo "可用的Build Tools版本："
    ls -la "$ANDROID_SDK/build-tools/"
fi

echo ""
echo "📋 下一步："
echo "1. 重启Android Studio"
echo "2. File → Sync Project with Gradle Files"
echo "3. 项目现在应该可以正常构建了！"




