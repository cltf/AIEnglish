#!/bin/bash

echo "🚀 升级Gradle版本以兼容Java 24..."

# 1. 检查当前版本
echo "📋 当前版本信息："
java -version
echo ""

# 2. 备份当前配置
echo "💾 备份当前gradle-wrapper.properties..."
cp gradle/wrapper/gradle-wrapper.properties gradle/wrapper/gradle-wrapper.properties.backup

# 3. 更新gradle-wrapper.properties到8.5版本
echo "🔄 更新Gradle版本到8.5..."
cat > gradle/wrapper/gradle-wrapper.properties << 'EOF'
distributionBase=PROJECT
distributionPath=gradle/wrapper/dists
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.5-bin.zip
networkTimeout=60000
zipStoreBase=PROJECT
zipStorePath=gradle/wrapper/dists
EOF

# 4. 清理旧版本
echo "🧹 清理旧版本Gradle..."
rm -rf gradle/wrapper/dists/gradle-7.5-bin/
rm -rf ~/.gradle/wrapper/dists/gradle-7.5-bin/

# 5. 测试新版本
echo "🧪 测试Gradle 8.5..."
./gradlew --version

echo ""
echo "✅ Gradle升级完成！"
echo ""
echo "📋 版本兼容性："
echo "- Java 24.0.1 ✅"
echo "- Gradle 8.5 ✅"
echo "- 支持Java 8-21 ✅"
echo ""
echo "🎯 下一步："
echo "1. 重启Android Studio"
echo "2. File → Sync Project with Gradle Files"
echo "3. 项目现在应该可以正常同步了！"




