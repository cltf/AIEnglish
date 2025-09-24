package com.vocabulary.scanner.ui.word

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vocabulary.scanner.databinding.ActivityWordDetailBinding
import com.vocabulary.scanner.data.UnifiedVocabularyDatabase

class WordDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityWordDetailBinding
    private var currentWord = ""
    private var unknownWords = arrayOf<String>()
    private var currentWordIndex = 0
    private var isAddedToVocabulary = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        currentWord = intent.getStringExtra("word") ?: ""
        unknownWords = intent.getStringArrayExtra("unknown_words") ?: arrayOf()
        currentWordIndex = unknownWords.indexOf(currentWord)
        
        setupWordDetail()
        setupClickListeners()
    }
    
    private fun setupWordDetail() {
        binding.tvWord.text = currentWord
        loadWordDefinition(currentWord)
        updateNavigationButtons()
    }
    
    private fun loadWordDefinition(word: String) {
        // 从词汇数据库查询
        val definition = UnifiedVocabularyDatabase.getDefinition(word)
        
        if (definition != null) {
            binding.tvPhonetic.text = definition.phonetic
            
            // 清空现有释义
            binding.layoutDefinitions.removeAllViews()
            
            // 添加释义
            definition.definitions.forEach { def ->
                val layout = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 16
                    }
                }
                
                // 添加符号
                val symbol = android.widget.TextView(this).apply {
                    text = "▶"
                    setTextColor(resources.getColor(android.R.color.holo_orange_light, null))
                    textSize = 14f
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 16
                    }
                }
                layout.addView(symbol)
                
                // 添加词性
                val pos = android.widget.TextView(this).apply {
                    text = "${def.partOfSpeech}："
                    setTextColor(resources.getColor(android.R.color.holo_orange_light, null))
                    textSize = 14f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 16
                    }
                }
                layout.addView(pos)
                
                // 添加释义
                val meaning = android.widget.TextView(this).apply {
                    text = def.meaning
                    textSize = 14f
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        0,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }
                layout.addView(meaning)
                
                binding.layoutDefinitions.addView(layout)
            }
        } else {
            // 如果词汇库中没有该词，显示默认信息
            binding.tvPhonetic.text = "/ˈʌnəʊn/"
            
            binding.layoutDefinitions.removeAllViews()
            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            
            val symbol = android.widget.TextView(this).apply {
                text = "▶"
                setTextColor(resources.getColor(android.R.color.holo_orange_light, null))
                textSize = 14f
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 16
                }
            }
            layout.addView(symbol)
            
            val meaning = android.widget.TextView(this).apply {
                text = "该词不在中考词汇库中"
                textSize = 14f
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }
            layout.addView(meaning)
            
            binding.layoutDefinitions.addView(layout)
        }
    }
    
    
    private fun updateNavigationButtons() {
        // 更新上一个按钮状态
        binding.btnPreviousWord.isEnabled = currentWordIndex > 0
        binding.btnPreviousWord.setTextColor(
            if (currentWordIndex > 0) {
                resources.getColor(android.R.color.holo_blue_light, null)
            } else {
                resources.getColor(android.R.color.darker_gray, null)
            }
        )
        
        // 更新下一个按钮状态
        binding.btnNextWord.isEnabled = currentWordIndex < unknownWords.size - 1
        binding.btnNextWord.setTextColor(
            if (currentWordIndex < unknownWords.size - 1) {
                resources.getColor(android.R.color.holo_blue_light, null)
            } else {
                resources.getColor(android.R.color.darker_gray, null)
            }
        )
    }
    
    private fun setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // 加入生词本按钮
        binding.btnAddToVocabulary.setOnClickListener {
            toggleAddToVocabulary()
        }
        
        
        // 上一个按钮
        binding.btnPreviousWord.setOnClickListener {
            if (currentWordIndex > 0) {
                currentWordIndex--
                currentWord = unknownWords[currentWordIndex]
                setupWordDetail()
            }
        }
        
        // 下一个按钮
        binding.btnNextWord.setOnClickListener {
            if (currentWordIndex < unknownWords.size - 1) {
                currentWordIndex++
                currentWord = unknownWords[currentWordIndex]
                setupWordDetail()
            }
        }
    }
    
    private fun toggleAddToVocabulary() {
        isAddedToVocabulary = !isAddedToVocabulary
        
        if (isAddedToVocabulary) {
            binding.btnAddToVocabulary.setImageResource(android.R.drawable.btn_star_big_on)
            binding.btnAddToVocabulary.setColorFilter(
                resources.getColor(android.R.color.holo_orange_light, null)
            )
            Toast.makeText(this, "已添加至生词本", Toast.LENGTH_SHORT).show()
            
            // TODO: 实际保存到生词本数据库
        } else {
            binding.btnAddToVocabulary.setImageResource(android.R.drawable.btn_star_big_off)
            binding.btnAddToVocabulary.setColorFilter(
                resources.getColor(android.R.color.darker_gray, null)
            )
            Toast.makeText(this, "已从生词本移除", Toast.LENGTH_SHORT).show()
        }
    }
    
}
