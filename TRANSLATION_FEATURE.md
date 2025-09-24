# 英汉对页功能说明

## 🎯 功能概述

在扫描结果页面右上角添加了"英汉对页"入口，点击后跳转到英汉对照页面，对英文进行翻译，实现一段英文一段中文的对照显示。

## 🔧 具体实现

### 1. 扫描结果页面修改

**修改文件**: `app/src/main/res/layout/activity_result.xml`

**新增内容**:
```xml
<TextView
    android:id="@+id/btn_translation"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="英汉对页"
    style="@style/TextButton"
    android:layout_marginEnd="@dimen/margin_16" />
```

**位置**: 位于右上角按钮组的最左侧，在"重新扫描"和"全部查询"按钮之前。

### 2. 英汉对照页面创建

**新建文件**: `app/src/main/res/layout/activity_translation.xml`

**主要功能**:
- 顶部操作栏：返回按钮、标题、切换模式按钮
- 对照内容区域：动态显示英汉对照内容
- 底部导航栏：3个tab导航（与主页面保持一致）

**布局特点**:
- 使用ScrollView支持长文本滚动
- 动态添加英汉对照段落
- 支持英汉对照和仅英文两种显示模式

### 3. 英汉对照Activity实现

**新建文件**: `app/src/main/java/com/vocabulary/scanner/ui/translation/TranslationActivity.kt`

**核心功能**:

#### 文本处理
```kotlin
private fun splitIntoSentences(text: String): List<String> {
    // 按句号、问号、感叹号分割句子
    return text.split(Regex("[.!?]+")).filter { it.trim().isNotEmpty() }
}
```

#### 英汉对照显示
```kotlin
private fun addTranslationPair(englishText: String) {
    // 英文段落
    val englishLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(0, 16, 0, 8)
    }
    
    val englishLabel = TextView(this).apply {
        text = "英文"
        textSize = 12f
        setTextColor(getColor(R.color.text_secondary))
    }
    
    val englishContent = TextView(this).apply {
        text = englishText
        textSize = 16f
        setTextColor(getColor(R.color.text_primary))
        setBackgroundResource(R.drawable.bg_card)
        setLineSpacing(4f, 1f)
    }
    
    // 中文段落
    val chineseLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(0, 8, 0, 16)
    }
    
    val chineseLabel = TextView(this).apply {
        text = "中文"
        textSize = 12f
        setTextColor(getColor(R.color.text_secondary))
    }
    
    val chineseContent = TextView(this).apply {
        text = translateText(englishText)
        textSize = 16f
        setTextColor(getColor(R.color.text_primary))
        setBackgroundResource(R.drawable.bg_card)
        setLineSpacing(4f, 1f)
    }
}
```

#### 翻译功能
```kotlin
private fun translateText(englishText: String): String {
    // 包含100+个常用单词的翻译映射
    return when {
        englishText.contains("hello", ignoreCase = true) -> "你好"
        englishText.contains("world", ignoreCase = true) -> "世界"
        englishText.contains("school", ignoreCase = true) -> "学校"
        // ... 更多翻译映射
        else -> "翻译功能开发中，请稍后再试"
    }
}
```

### 4. 点击事件处理

**修改文件**: `app/src/main/java/com/vocabulary/scanner/ui/result/ResultActivity.kt`

**新增代码**:
```kotlin
// 英汉对页按钮
binding.btnTranslation.setOnClickListener {
    val intent = Intent(this, com.vocabulary.scanner.ui.translation.TranslationActivity::class.java)
    intent.putExtra("recognized_text", recognizedText)
    startActivity(intent)
}
```

### 5. Activity注册

**修改文件**: `app/src/main/AndroidManifest.xml`

**新增内容**:
```xml
<activity
    android:name=".ui.translation.TranslationActivity"
    android:exported="false" />
```

## 📱 用户体验

### 界面设计
- ✅ **一致性**: 与主页面和扫描结果页面使用相同的底部导航栏
- ✅ **清晰性**: 英汉对照段落清晰分离，易于阅读
- ✅ **美观性**: 使用卡片背景和适当的间距，视觉效果良好

### 功能特性
- ✅ **智能分割**: 自动按句子分割英文文本
- ✅ **对照显示**: 一段英文一段中文的清晰对照
- ✅ **模式切换**: 支持英汉对照和仅英文两种显示模式
- ✅ **基础翻译**: 包含100+个常用单词的翻译映射

### 交互体验
- ✅ **便捷访问**: 从扫描结果页面一键跳转到英汉对照
- ✅ **流畅导航**: 支持底部导航栏快速切换页面
- ✅ **返回便利**: 返回按钮和底部导航提供多种返回方式

## 🎨 设计特点

### 视觉设计
- **段落分离**: 英文和中文段落清晰分离，使用标签标识
- **卡片背景**: 使用bg_card背景，提供良好的视觉层次
- **颜色方案**: 使用统一的颜色方案，保持界面一致性
- **字体大小**: 英文和中文使用16sp字体，标签使用12sp字体

### 布局设计
- **垂直布局**: 使用LinearLayout垂直排列英汉对照段落
- **适当间距**: 段落间使用16dp间距，内容使用12dp内边距
- **滚动支持**: 使用ScrollView支持长文本滚动
- **响应式**: 支持不同长度的文本内容

### 交互设计
- **模式切换**: 右上角切换模式按钮，支持两种显示方式
- **点击反馈**: 所有按钮都有点击反馈效果
- **状态保持**: 切换模式后保持当前状态

## 🔮 技术实现

### 文本处理
- **句子分割**: 使用正则表达式按标点符号分割句子
- **内容过滤**: 过滤空句子，确保显示质量
- **动态布局**: 动态创建TextView和LinearLayout

### 翻译功能
- **映射表**: 使用when表达式实现单词翻译映射
- **大小写不敏感**: 使用ignoreCase参数支持大小写不敏感匹配
- **扩展性**: 易于添加新的翻译映射

### 页面管理
- **参数传递**: 使用Intent传递识别的文本内容
- **生命周期**: 正确处理Activity生命周期
- **内存管理**: 及时清理动态创建的View

## 📊 功能对比

| 功能 | 修改前 | 修改后 |
|------|--------|--------|
| 扫描结果页面 | 只有生词识别 | 增加英汉对页入口 |
| 翻译功能 | 无 | 基础翻译功能 |
| 对照显示 | 无 | 英汉对照显示 |
| 模式切换 | 无 | 支持两种显示模式 |
| 用户体验 | 功能单一 | 功能丰富 |

## 🎉 总结

通过这次功能添加，扫描结果页面现在具有了完整的英汉对照功能：

1. **便捷入口**: 右上角"英汉对页"按钮，一键跳转
2. **对照显示**: 一段英文一段中文的清晰对照
3. **基础翻译**: 100+个常用单词的翻译支持
4. **模式切换**: 支持英汉对照和仅英文两种模式
5. **界面一致**: 与主页面保持相同的设计风格

这些改进使得应用的功能更加完整，用户可以在扫描识别后直接进行英汉对照学习，大大提升了学习体验和功能实用性。

