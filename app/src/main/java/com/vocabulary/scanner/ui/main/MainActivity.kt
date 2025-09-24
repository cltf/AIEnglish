package com.vocabulary.scanner.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vocabulary.scanner.databinding.ActivityMainBinding
import com.vocabulary.scanner.ui.scan.ScanActivity
import com.vocabulary.scanner.ui.vocabulary.VocabularyActivity
import com.vocabulary.scanner.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // 扫描阅读按钮
        binding.btnScanReading.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }
        
        // 我的生词本按钮
        binding.btnMyVocabulary.setOnClickListener {
            val intent = Intent(this, VocabularyActivity::class.java)
            startActivity(intent)
        }
        
        // 词库查询按钮（暂时跳转到生词本，后续可扩展）
        binding.btnWordLookup.setOnClickListener {
            val intent = Intent(this, VocabularyActivity::class.java)
            startActivity(intent)
        }
        
        // 设置按钮
        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        
        // 反馈按钮
        binding.btnFeedback.setOnClickListener {
            showFeedbackDialog()
        }
    }
    
    private fun showFeedbackDialog() {
        // TODO: 实现反馈对话框
        // 这里可以显示一个对话框让用户输入反馈意见和邮箱
        android.widget.Toast.makeText(this, "反馈功能待实现", android.widget.Toast.LENGTH_SHORT).show()
    }
}





