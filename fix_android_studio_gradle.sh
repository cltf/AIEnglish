#!/bin/bash

echo "🔧 修复Android Studio Gradle权限问题..."

# 1. 设置环境变量
export GRADLE_USER_HOME=$HOME/.gradle
echo "✅ 设置GRADLE_USER_HOME: $GRADLE_USER_HOME"

# 2. 创建必要的目录
mkdir -p ~/.gradle
mkdir -p gradle/wrapper/dists
echo "✅ 创建目录结构"

# 3. 创建全局gradle.properties
cat > ~/.gradle/gradle.properties << 'EOF'
# 全局Gradle配置
org.gradle.user.home=/Users/chenlei/.gradle
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true

# 网络配置
systemProp.http.connectionTimeout=60000
systemProp.http.socketTimeout=60000

# 强制使用用户目录
gradle.user.home=/Users/chenlei/.gradle
EOF
echo "✅ 创建全局gradle.properties"

# 4. 修改gradle-wrapper.properties使用项目本地目录
cat > gradle/wrapper/gradle-wrapper.properties << 'EOF'
distributionBase=PROJECT
distributionPath=gradle/wrapper/dists
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-7.5-bin.zip
networkTimeout=60000
zipStoreBase=PROJECT
zipStorePath=gradle/wrapper/dists
EOF
echo "✅ 更新gradle-wrapper.properties"

# 5. 清理可能损坏的缓存
echo "🧹 清理缓存..."
rm -rf ~/.gradle/caches/
rm -rf gradle/wrapper/dists/gradle-7.5-bin/

# 6. 重新创建目录
mkdir -p ~/.gradle/caches
mkdir -p gradle/wrapper/dists

# 7. 设置权限
chmod -R 755 ~/.gradle
chmod -R 755 gradle/
echo "✅ 设置权限"

# 8. 测试Gradle
echo "🧪 测试Gradle..."
./gradlew --version

echo ""
echo "🎉 修复完成！"
echo ""
echo "📋 下一步操作："
echo "1. 重启Android Studio"
echo "2. 在Android Studio中：File → Sync Project with Gradle Files"
echo "3. 如果还有问题，检查Android Studio的Gradle设置："
echo "   File → Settings → Build → Gradle"
echo "   确保'Gradle user home'设置为: $HOME/.gradle"




