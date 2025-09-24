#!/bin/bash

echo "🎨 创建应用图标..."

# 创建简单的应用图标（使用Android Studio默认图标）
# 这里我们创建一个简单的XML图标作为占位符

# 创建ic_launcher.xml
cat > app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

# 创建ic_launcher_round.xml
cat > app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

# 创建颜色资源
cat > app/src/main/res/values/ic_launcher_background.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#3DDC84</color>
</resources>
EOF

# 创建前景图标
cat > app/src/main/res/drawable/ic_launcher_foreground.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#00000000"
        android:pathData="M0,0h108v108h-108z" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M54,54m-48,0a48,48 0,1 1,96 0a48,48 0,1 1,-96 0" />
    <path
        android:fillColor="#3DDC84"
        android:pathData="M54,30c-13.2,0 -24,10.8 -24,24s10.8,24 24,24s24,-10.8 24,-24S67.2,30 54,30zM54,66c-6.6,0 -12,-5.4 -12,-12s5.4,-12 12,-12s12,5.4 12,12S60.6,66 54,66z" />
</vector>
EOF

# 创建传统图标（用于旧版本Android）
cat > app/src/main/res/mipmap-hdpi/ic_launcher.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="72dp"
    android:height="72dp"
    android:viewportWidth="72"
    android:viewportHeight="72">
    <path
        android:fillColor="#3DDC84"
        android:pathData="M0,0h72v72h-72z" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M36,36m-32,0a32,32 0,1 1,64 0a32,32 0,1 1,-64 0" />
    <path
        android:fillColor="#3DDC84"
        android:pathData="M36,20c-8.8,0 -16,7.2 -16,16s7.2,16 16,16s16,-7.2 16,-16S44.8,20 36,20zM36,44c-4.4,0 -8,-3.6 -8,-8s3.6,-8 8,-8s8,3.6 8,8S40.4,44 36,44z" />
</vector>
EOF

# 复制到其他密度目录
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-mdpi/ic_launcher.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xhdpi/ic_launcher.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xxhdpi/ic_launcher.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xxxhdpi/ic_launcher.xml

# 创建圆形图标
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-hdpi/ic_launcher_round.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-mdpi/ic_launcher_round.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xhdpi/ic_launcher_round.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xxhdpi/ic_launcher_round.xml
cp app/src/main/res/mipmap-hdpi/ic_launcher.xml app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.xml

echo "✅ 应用图标创建完成！"
echo "现在可以重新构建项目了。"




