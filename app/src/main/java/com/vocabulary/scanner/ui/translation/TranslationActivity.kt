package com.vocabulary.scanner.ui.translation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vocabulary.scanner.R
import com.vocabulary.scanner.databinding.ActivityTranslationBinding
import com.vocabulary.scanner.service.TranslationService
import com.vocabulary.scanner.ui.settings.TranslationSettingsActivity
import com.vocabulary.scanner.utils.TextParser
import kotlinx.coroutines.launch

class TranslationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTranslationBinding
    private var originalText = ""
    private var isTranslationMode = true // true: 英汉对照, false: 仅英文
    private lateinit var translationService: TranslationService
    
    companion object {
        private const val PREFS_NAME = "translation_settings"
        private const val KEY_TRANSLATION_PROVIDER = "translation_provider"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化翻译服务
        translationService = TranslationService(this)
        
        // 获取传入的文本
        originalText = intent.getStringExtra("recognized_text") ?: ""
        
        setupClickListeners()
        generateTranslationContent()
    }
    
    private fun setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // 切换模式按钮
        binding.btnSwitchMode.setOnClickListener {
            isTranslationMode = !isTranslationMode
            binding.btnSwitchMode.text = if (isTranslationMode) "仅英文" else "英汉对照"
            generateTranslationContent()
        }
        
        // 中考词汇tab
        binding.navVocabulary.setOnClickListener {
            val intent = Intent(this, com.vocabulary.scanner.ui.main.MainTabsActivity::class.java)
            intent.putExtra("initial_tab", 0)
            startActivity(intent)
            finish()
        }
        
        // 扫描tab
        binding.navScan.setOnClickListener {
            val intent = Intent(this, com.vocabulary.scanner.ui.main.MainTabsActivity::class.java)
            intent.putExtra("initial_tab", 1)
            startActivity(intent)
            finish()
        }
        
        // 我的tab
        binding.navProfile.setOnClickListener {
            val intent = Intent(this, com.vocabulary.scanner.ui.main.MainTabsActivity::class.java)
            intent.putExtra("initial_tab", 2)
            startActivity(intent)
            finish()
        }
    }
    
    private fun generateTranslationContent() {
        binding.layoutTranslationContent.removeAllViews()
        
        if (originalText.isEmpty()) {
            val emptyTextView = TextView(this).apply {
                text = "没有可翻译的内容"
                textSize = 16f
                setTextColor(getColor(R.color.text_secondary))
                setPadding(0, 32, 0, 32)
            }
            binding.layoutTranslationContent.addView(emptyTextView)
            return
        }
        
        // 将文本按句子分割
        val sentences = TextParser.splitIntoSentences(originalText)
        
        sentences.forEach { sentence ->
            if (sentence.trim().isNotEmpty()) {
                if (isTranslationMode) {
                    // 英汉对照模式
                    addTranslationPair(sentence.trim())
                } else {
                    // 仅英文模式
                    addEnglishOnly(sentence.trim())
                }
            }
        }
    }
    
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
            setPadding(0, 0, 0, 4)
        }
        
        val englishContent = TextView(this).apply {
            text = englishText
            textSize = 16f
            setTextColor(getColor(R.color.text_primary))
            setPadding(16, 12, 16, 12)
            setBackgroundResource(R.drawable.bg_card)
            setLineSpacing(4f, 1f)
        }
        
        englishLayout.addView(englishLabel)
        englishLayout.addView(englishContent)
        
        // 中文段落
        val chineseLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 8, 0, 16)
        }
        
        val chineseLabel = TextView(this).apply {
            text = "中文"
            textSize = 12f
            setTextColor(getColor(R.color.text_secondary))
            setPadding(0, 0, 0, 4)
        }
        
        val chineseContent = TextView(this).apply {
            text = "翻译中..."
            textSize = 16f
            setTextColor(getColor(R.color.text_primary))
            setPadding(16, 12, 16, 12)
            setBackgroundResource(R.drawable.bg_card)
            setLineSpacing(4f, 1f)
        }
        
        chineseLayout.addView(chineseLabel)
        chineseLayout.addView(chineseContent)
        
        binding.layoutTranslationContent.addView(englishLayout)
        binding.layoutTranslationContent.addView(chineseLayout)
        
        // 异步翻译
        lifecycleScope.launch {
            try {
                val result = translationService.translate(
                    englishText,
                    from = "en",
                    to = "zh",
                    provider = getSelectedTranslationProvider()
                )
                
                if (result.success) {
                    chineseContent.text = result.translatedText
                } else {
                    chineseContent.text = "翻译失败：${result.error}"
                    chineseContent.setTextColor(getColor(R.color.error_color))
                }
            } catch (e: Exception) {
                chineseContent.text = "翻译出错：${e.message}"
                chineseContent.setTextColor(getColor(R.color.error_color))
            }
        }
    }
    
    private fun addEnglishOnly(englishText: String) {
        val englishLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 16, 0, 16)
        }
        
        val englishContent = TextView(this).apply {
            text = englishText
            textSize = 16f
            setTextColor(getColor(R.color.text_primary))
            setPadding(16, 12, 16, 12)
            setBackgroundResource(R.drawable.bg_card)
            setLineSpacing(4f, 1f)
        }
        
        englishLayout.addView(englishContent)
        binding.layoutTranslationContent.addView(englishLayout)
    }
    
    
    private fun getSelectedTranslationProvider(): TranslationService.TranslationProvider {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val provider = sharedPreferences.getString(KEY_TRANSLATION_PROVIDER, "LOCAL")
        return when (provider) {
            "BAIDU" -> TranslationService.TranslationProvider.BAIDU
            "GOOGLE" -> TranslationService.TranslationProvider.GOOGLE
            "YOUDAO" -> TranslationService.TranslationProvider.YOUDAO
            else -> TranslationService.TranslationProvider.LOCAL
        }
    }
    
}
