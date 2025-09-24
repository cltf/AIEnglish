#!/bin/bash

echo "🔍 检查Android SDK配置..."

# 检查常见的Android SDK路径
SDK_PATHS=(
    "$HOME/Library/Android/sdk"
    "/Users/$USER/Library/Android/sdk"
    "/Applications/Android Studio.app/Contents/sdk"
    "$ANDROID_HOME"
)

echo "📂 查找Android SDK路径..."
for path in "${SDK_PATHS[@]}"; do
    if [ -d "$path" ]; then
        echo "✅ 找到Android SDK: $path"
        export ANDROID_HOME="$path"
        break
    fi
done

if [ -z "$ANDROID_HOME" ]; then
    echo "❌ 未找到Android SDK"
    echo "💡 请确保已安装Android Studio和Android SDK"
    echo "📱 安装路径通常是: ~/Library/Android/sdk"
    exit 1
fi

echo "🔧 设置环境变量..."
export PATH="$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools"

# 检查关键组件
echo "📋 检查SDK组件..."

components=(
    "platform-tools"
    "build-tools/33.0.0"
    "platforms/android-33"
)

for component in "${components[@]}"; do
    if [ -d "$ANDROID_HOME/$component" ]; then
        echo "✅ $component - 已安装"
    else
        echo "❌ $component - 未安装"
    fi
done

echo "🎯 建议的Android Studio配置:"
echo "Android SDK Location: $ANDROID_HOME"
echo "JDK Location: /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"

echo ""
echo "📝 在Android Studio中设置:"
echo "1. File → Project Structure → SDK Location"
echo "2. 设置 Android SDK Location: $ANDROID_HOME"
echo "3. 设置 JDK Location: /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
echo "4. 点击 Apply → OK"





