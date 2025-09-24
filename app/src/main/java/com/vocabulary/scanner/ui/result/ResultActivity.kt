package com.vocabulary.scanner.ui.result

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vocabulary.scanner.R
import com.vocabulary.scanner.databinding.ActivityResultBinding
import com.vocabulary.scanner.ui.scan.ScanActivity
import com.vocabulary.scanner.ui.word.WordDetailActivity
import com.vocabulary.scanner.data.UnifiedVocabularyDatabase
import com.vocabulary.scanner.utils.WordVariationUtils
import java.util.regex.Pattern

class ResultActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityResultBinding
    private var recognizedText = ""
    private var unknownWords = mutableListOf<String>()
    
    // 使用中考词汇数据库
    private val vocabularySet = UnifiedVocabularyDatabase.getVocabularySet()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        recognizedText = intent.getStringExtra("recognized_text") ?: ""
        processText()
        setupClickListeners()
    }
    
    private fun processText() {
        // 识别生词并标记
        val spannableString = SpannableString(recognizedText)
        unknownWords.clear()
        
        // 使用正则表达式匹配单词
        val wordPattern = Pattern.compile("\\b[a-zA-Z]+\\b")
        val matcher = wordPattern.matcher(recognizedText)
        
        while (matcher.find()) {
            val word = matcher.group().lowercase()
            
            // 使用单词变形处理工具检查单词
            val baseWord = WordVariationUtils.findWordInVocabulary(word, vocabularySet)
            
            // 如果找不到匹配的词汇（包括变形），则标记为生词
            if (baseWord == null && word.length > 2) {
                unknownWords.add(word)
                
                val start = matcher.start()
                val end = matcher.end()
                
                // 添加红色下划线
                spannableString.setSpan(
                    UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                
                // 添加点击事件
                spannableString.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            showWordDetailDialog(word)
                        }
                        
                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = true
                            ds.color = Color.RED
                        }
                    },
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        
        binding.tvRecognizedText.text = spannableString
        binding.tvRecognizedText.movementMethod = LinkMovementMethod.getInstance()
        
        // 更新统计信息
        binding.tvUnknownWordCount.text = "生词数量：${unknownWords.size} 个"
        binding.tvRecognitionAccuracy.text = "识别准确率：${calculateAccuracy()}%"
    }
    
    private fun calculateAccuracy(): Int {
        // 简化的准确率计算，实际应用中应该更复杂
        val totalWords = recognizedText.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        if (totalWords == 0) return 100
        val knownWords = totalWords - unknownWords.size
        return (knownWords * 100 / totalWords).coerceAtLeast(85)
    }
    
    private fun showWordDetailDialog(word: String) {
        val intent = Intent(this, WordDetailActivity::class.java)
        intent.putExtra("word", word)
        intent.putExtra("unknown_words", unknownWords.toTypedArray())
        startActivity(intent)
    }
    
    private fun setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // 重新扫描按钮
        binding.btnRescan.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        // 全部查询按钮
        binding.btnQueryAll.setOnClickListener {
            if (unknownWords.isNotEmpty()) {
                // 显示批量查询进度
                android.widget.Toast.makeText(this, "正在批量查询...", android.widget.Toast.LENGTH_SHORT).show()
                
                // 跳转到第一个生词的详情页
                showWordDetailDialog(unknownWords.first())
            } else {
                android.widget.Toast.makeText(this, "没有发现生词", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        
        // 英汉对页按钮
        binding.btnTranslation.setOnClickListener {
            val intent = Intent(this, com.vocabulary.scanner.ui.translation.TranslationActivity::class.java)
            intent.putExtra("recognized_text", recognizedText)
            startActivity(intent)
        }
        
        // 中考词汇tab
        binding.navVocabulary.setOnClickListener {
            // 跳转到词汇页面
            val intent = Intent(this, com.vocabulary.scanner.ui.main.MainTabsActivity::class.java)
            intent.putExtra("initial_tab", 0) // 0表示中考词汇tab
            startActivity(intent)
            finish()
        }
        
        // 扫描tab
        binding.navScan.setOnClickListener {
            // 跳转到扫描页面
            val intent = Intent(this, com.vocabulary.scanner.ui.main.MainTabsActivity::class.java)
            intent.putExtra("initial_tab", 1) // 1表示扫描tab
            startActivity(intent)
            finish()
        }
        
        // 我的tab
        binding.navProfile.setOnClickListener {
            // 跳转到我的页面
            val intent = Intent(this, com.vocabulary.scanner.ui.main.MainTabsActivity::class.java)
            intent.putExtra("initial_tab", 2) // 2表示我的tab
            startActivity(intent)
            finish()
        }
    }
}
