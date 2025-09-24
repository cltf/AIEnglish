#!/bin/bash

echo "🔧 下载正确的gradle-wrapper.jar..."

# 删除错误的文件
rm -f gradle/wrapper/gradle-wrapper.jar

# 创建临时目录
mkdir -p temp_gradle

echo "📥 下载Gradle 8.2完整版本..."
cd temp_gradle

# 下载Gradle完整版本
if curl -L --connect-timeout 60 -o gradle-8.2-bin.zip "https://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip"; then
    echo "✅ Gradle下载成功！"
    
    # 解压
    if unzip -q gradle-8.2-bin.zip; then
        echo "✅ 解压成功！"
        
        # 复制gradle-wrapper.jar
        if cp gradle-8.2/wrapper/gradle-wrapper.jar ../gradle/wrapper/; then
            echo "✅ gradle-wrapper.jar复制成功！"
            
            # 设置权限
            chmod +x ../gradlew
            chmod +x ../gradlew.bat
            
            echo "🎉 Gradle Wrapper安装完成！"
        else
            echo "❌ 复制gradle-wrapper.jar失败！"
        fi
    else
        echo "❌ 解压失败！"
    fi
else
    echo "❌ Gradle下载失败！"
fi

# 清理临时文件
cd ..
rm -rf temp_gradle

echo "🧹 清理完成！"





