package com.vocabulary.scanner.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vocabulary.scanner.databinding.FragmentProfileBinding
import com.vocabulary.scanner.ui.settings.SettingsActivity
import com.vocabulary.scanner.ui.vocabulary.VocabularyActivity
import com.vocabulary.scanner.ui.import.ImportVocabularyActivity

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        updateStatistics()
    }
    
    private fun setupClickListeners() {
        // 生词本
        binding.layoutVocabulary.setOnClickListener {
            val intent = Intent(requireContext(), VocabularyActivity::class.java)
            startActivity(intent)
        }
        
        // 导入词汇 - 暂时注释掉，因为布局中没有这个ID
        // binding.layoutImportVocabulary?.setOnClickListener {
        //     val intent = Intent(requireContext(), ImportVocabularyActivity::class.java)
        //     startActivity(intent)
        // }
        
        // 设置
        binding.layoutSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
        
        // 反馈
        binding.layoutFeedback.setOnClickListener {
            showFeedbackDialog()
        }
        
        // 关于
        binding.layoutAbout.setOnClickListener {
            showAboutDialog()
        }
    }
    
    private fun updateStatistics() {
        // TODO: 从数据库获取实际的学习统计数据
        // 这里暂时使用模拟数据
        val masteredCount = 0
        val learningCount = 0
        val totalCount = 1600
        
        // 更新统计显示
        // 注意：这里需要在布局中添加对应的TextView ID
        // binding.tvMasteredCount.text = masteredCount.toString()
        // binding.tvLearningCount.text = learningCount.toString()
        // binding.tvTotalCount.text = totalCount.toString()
    }
    
    private fun showFeedbackDialog() {
        // TODO: 实现反馈对话框
        android.widget.Toast.makeText(requireContext(), "反馈功能待实现", android.widget.Toast.LENGTH_SHORT).show()
    }
    
    private fun showAboutDialog() {
        // TODO: 实现关于对话框
        android.widget.Toast.makeText(requireContext(), "关于应用功能待实现", android.widget.Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

