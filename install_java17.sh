#!/bin/bash

echo "☕ 安装Java 17以解决Gradle兼容性问题..."

# 检查是否已安装Homebrew
if ! command -v brew &> /dev/null; then
    echo "❌ 未找到Homebrew，请先安装Homebrew"
    echo "💡 运行: /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
    exit 1
fi

echo "📦 安装OpenJDK 17..."
brew install openjdk@17

if [ $? -eq 0 ]; then
    echo "✅ OpenJDK 17安装成功！"
    
    # 设置JAVA_HOME
    export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
    
    # 添加到shell配置文件
    echo "🔧 配置环境变量..."
    
    # 检测shell类型
    if [ "$SHELL" = "/bin/zsh" ]; then
        SHELL_CONFIG="$HOME/.zshrc"
    else
        SHELL_CONFIG="$HOME/.bash_profile"
    fi
    
    # 添加Java 17到PATH
    echo "" >> "$SHELL_CONFIG"
    echo "# Java 17 for Android development" >> "$SHELL_CONFIG"
    echo "export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home" >> "$SHELL_CONFIG"
    echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> "$SHELL_CONFIG"
    
    echo "✅ 环境变量已添加到 $SHELL_CONFIG"
    
    # 验证安装
    echo "🔍 验证Java版本..."
    "$JAVA_HOME/bin/java" -version
    
    if [ $? -eq 0 ]; then
        echo "🎉 Java 17安装完成！"
        echo "💡 请重新打开终端或运行: source $SHELL_CONFIG"
        echo "🚀 然后运行: ./gradlew clean build"
    else
        echo "❌ Java 17安装验证失败"
    fi
else
    echo "❌ OpenJDK 17安装失败"
    exit 1
fi





