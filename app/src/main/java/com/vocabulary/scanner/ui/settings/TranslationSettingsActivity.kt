package com.vocabulary.scanner.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vocabulary.scanner.R
import com.vocabulary.scanner.databinding.ActivityTranslationSettingsBinding
import com.vocabulary.scanner.service.TranslationService
import com.vocabulary.scanner.service.TranslationProvider
import kotlinx.coroutines.launch

/**
 * 翻译设置页面
 * 允许用户选择翻译服务提供商和配置API密钥
 */
class TranslationSettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTranslationSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    
    companion object {
        private const val PREFS_NAME = "translation_settings"
        private const val KEY_TRANSLATION_PROVIDER = "translation_provider"
        private const val KEY_BAIDU_APP_ID = "baidu_app_id"
        private const val KEY_BAIDU_SECRET_KEY = "baidu_secret_key"
        private const val KEY_GOOGLE_API_KEY = "google_api_key"
        private const val KEY_YOUDAO_APP_KEY = "youdao_app_key"
        private const val KEY_YOUDAO_APP_SECRET = "youdao_app_secret"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslationSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        setupClickListeners()
        loadSettings()
    }
    
    private fun setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // 保存设置
        binding.btnSave.setOnClickListener {
            saveSettings()
        }
        
        // 测试翻译
        binding.btnTestTranslation.setOnClickListener {
            testTranslation()
        }
        
        // 翻译服务选择
        binding.radioBaidu.setOnClickListener {
            updateProviderSelection(TranslationProvider.BAIDU)
        }
        
        binding.radioGoogle.setOnClickListener {
            updateProviderSelection(TranslationProvider.GOOGLE)
        }
        
        binding.radioYoudao.setOnClickListener {
            updateProviderSelection(TranslationProvider.YOUDAO)
        }
        
        binding.radioLocal.setOnClickListener {
            updateProviderSelection(TranslationProvider.LOCAL)
        }
    }
    
    private fun loadSettings() {
        // 加载翻译服务提供商
        val provider = sharedPreferences.getString(KEY_TRANSLATION_PROVIDER, "LOCAL")
        when (provider) {
            "BAIDU" -> binding.radioBaidu.isChecked = true
            "GOOGLE" -> binding.radioGoogle.isChecked = true
            "YOUDAO" -> binding.radioYoudao.isChecked = true
            else -> binding.radioLocal.isChecked = true
        }
        
        // 加载API密钥
        binding.etBaiduAppId.setText(sharedPreferences.getString(KEY_BAIDU_APP_ID, ""))
        binding.etBaiduSecretKey.setText(sharedPreferences.getString(KEY_BAIDU_SECRET_KEY, ""))
        binding.etGoogleApiKey.setText(sharedPreferences.getString(KEY_GOOGLE_API_KEY, ""))
        binding.etYoudaoAppKey.setText(sharedPreferences.getString(KEY_YOUDAO_APP_KEY, ""))
        binding.etYoudaoAppSecret.setText(sharedPreferences.getString(KEY_YOUDAO_APP_SECRET, ""))
        
        updateProviderSelection(getCurrentProvider())
    }
    
    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        
        // 保存翻译服务提供商
        val provider = getCurrentProvider()
        editor.putString(KEY_TRANSLATION_PROVIDER, provider.name)
        
        // 保存API密钥
        editor.putString(KEY_BAIDU_APP_ID, binding.etBaiduAppId.text.toString())
        editor.putString(KEY_BAIDU_SECRET_KEY, binding.etBaiduSecretKey.text.toString())
        editor.putString(KEY_GOOGLE_API_KEY, binding.etGoogleApiKey.text.toString())
        editor.putString(KEY_YOUDAO_APP_KEY, binding.etYoudaoAppKey.text.toString())
        editor.putString(KEY_YOUDAO_APP_SECRET, binding.etYoudaoAppSecret.text.toString())
        
        editor.apply()
        
        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show()
    }
    
    private fun testTranslation() {
        val testText = "Hello, world!"
        val translationService = TranslationService(this)
        
        // 使用协程执行翻译测试
        lifecycleScope.launch {
            try {
                val result = translationService.translate(
                    testText,
                    from = "en",
                    to = "zh",
                    provider = getCurrentProvider()
                )
                
                if (result.success) {
                    Toast.makeText(
                        this@TranslationSettingsActivity,
                        "测试成功：$testText -> ${result.translatedText}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@TranslationSettingsActivity,
                        "测试失败：${result.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@TranslationSettingsActivity,
                    "测试出错：${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun getCurrentProvider(): TranslationProvider {
        return when {
            binding.radioBaidu.isChecked -> TranslationProvider.BAIDU
            binding.radioGoogle.isChecked -> TranslationProvider.GOOGLE
            binding.radioYoudao.isChecked -> TranslationProvider.YOUDAO
            else -> TranslationProvider.LOCAL
        }
    }
    
    private fun updateProviderSelection(provider: TranslationProvider) {
        // 显示/隐藏相应的API配置区域
        when (provider) {
            TranslationProvider.BAIDU -> {
                binding.layoutBaiduConfig.visibility = android.view.View.VISIBLE
                binding.layoutGoogleConfig.visibility = android.view.View.GONE
                binding.layoutYoudaoConfig.visibility = android.view.View.GONE
            }
            TranslationProvider.GOOGLE -> {
                binding.layoutBaiduConfig.visibility = android.view.View.GONE
                binding.layoutGoogleConfig.visibility = android.view.View.VISIBLE
                binding.layoutYoudaoConfig.visibility = android.view.View.GONE
            }
            TranslationProvider.YOUDAO -> {
                binding.layoutBaiduConfig.visibility = android.view.View.GONE
                binding.layoutGoogleConfig.visibility = android.view.View.GONE
                binding.layoutYoudaoConfig.visibility = android.view.View.VISIBLE
            }
            TranslationProvider.LOCAL -> {
                binding.layoutBaiduConfig.visibility = android.view.View.GONE
                binding.layoutGoogleConfig.visibility = android.view.View.GONE
                binding.layoutYoudaoConfig.visibility = android.view.View.GONE
            }
        }
    }
}
