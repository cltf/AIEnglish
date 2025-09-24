package com.vocabulary.scanner.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.widget.ImageView
import android.widget.TextView
import com.vocabulary.scanner.R
import com.vocabulary.scanner.databinding.ActivityMainTabsBinding
import com.vocabulary.scanner.ui.profile.ProfileFragment
import com.vocabulary.scanner.ui.scan.ScanFragment
import com.vocabulary.scanner.ui.vocabulary.VocabularyFragment

class MainTabsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainTabsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTabsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        setupBottomNavigation()
        
        // 处理初始tab设置
        val initialTab = intent.getIntExtra("initial_tab", 1) // 默认为扫描tab
        switchToTab(initialTab)
    }
    
    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        // 确保ViewPager2可以正常滑动
        binding.viewPager.isUserInputEnabled = true
        binding.viewPager.offscreenPageLimit = 2
        
        // 确保ViewPager2不会拦截底部导航的触摸事件
        binding.viewPager.setOnTouchListener { _, event ->
            // 如果触摸事件在底部导航区域，不拦截
            val bottomNavTop = binding.bottomNavigation.top
            if (event.rawY >= bottomNavTop) {
                return@setOnTouchListener false
            }
            false
        }
    }
    
    private fun setupBottomNavigation() {
        android.util.Log.d("MainTabsActivity", "Setting up custom bottom navigation")
        
        // 确保底部导航可以接收触摸事件
        binding.bottomNavigation.isClickable = true
        binding.bottomNavigation.isFocusable = true
        binding.bottomNavigation.isFocusableInTouchMode = true
        
        // 确保每个Tab都能接收触摸事件
        binding.navVocabulary.isClickable = true
        binding.navVocabulary.isFocusable = true
        binding.navScan.isClickable = true
        binding.navScan.isFocusable = true
        binding.navProfile.isClickable = true
        binding.navProfile.isFocusable = true
        
        // 设置点击监听器
        binding.navVocabulary.setOnClickListener {
            android.util.Log.d("MainTabsActivity", "Vocabulary tab clicked")
            switchToTab(0)
        }
        
        binding.navScan.setOnClickListener {
            android.util.Log.d("MainTabsActivity", "Scan tab clicked")
            switchToTab(1)
        }
        
        binding.navProfile.setOnClickListener {
            android.util.Log.d("MainTabsActivity", "Profile tab clicked")
            switchToTab(2)
        }
        
        // 添加触摸事件监听器进行调试
        binding.navVocabulary.setOnTouchListener { _, event ->
            android.util.Log.d("MainTabsActivity", "Vocabulary touch event: ${event.action}")
            false
        }
        
        binding.navScan.setOnTouchListener { _, event ->
            android.util.Log.d("MainTabsActivity", "Scan touch event: ${event.action}")
            false
        }
        
        binding.navProfile.setOnTouchListener { _, event ->
            android.util.Log.d("MainTabsActivity", "Profile touch event: ${event.action}")
            false
        }
        
        // 设置默认选中第一个Tab
        updateTabSelection(0)
        android.util.Log.d("MainTabsActivity", "Default tab set to vocabulary")
        
        // 监听ViewPager2的页面变化，同步更新底部导航
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTabSelection(position)
            }
        })
    }
    
    private fun updateTabSelection(position: Int) {
        // 重置所有Tab的颜色
        binding.iconVocabulary.setColorFilter(getColor(R.color.text_secondary))
        binding.textVocabulary.setTextColor(getColor(R.color.text_secondary))
        binding.iconScan.setColorFilter(getColor(R.color.text_secondary))
        binding.textScan.setTextColor(getColor(R.color.text_secondary))
        binding.iconProfile.setColorFilter(getColor(R.color.text_secondary))
        binding.textProfile.setTextColor(getColor(R.color.text_secondary))
        
        // 设置当前选中Tab的颜色
        when (position) {
            0 -> {
                binding.iconVocabulary.setColorFilter(getColor(R.color.primary_blue))
                binding.textVocabulary.setTextColor(getColor(R.color.primary_blue))
            }
            1 -> {
                binding.iconScan.setColorFilter(getColor(R.color.primary_blue))
                binding.textScan.setTextColor(getColor(R.color.primary_blue))
            }
            2 -> {
                binding.iconProfile.setColorFilter(getColor(R.color.primary_blue))
                binding.textProfile.setTextColor(getColor(R.color.primary_blue))
            }
        }
    }
    
    fun switchToTab(position: Int) {
        binding.viewPager.currentItem = position
    }
    
    private class MainPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            android.util.Log.d("MainTabsActivity", "Creating fragment for position: $position")
            return when (position) {
                0 -> {
                    android.util.Log.d("MainTabsActivity", "Creating VocabularyFragment")
                    VocabularyFragment()
                }
                1 -> {
                    android.util.Log.d("MainTabsActivity", "Creating ScanFragment")
                    ScanFragment()
                }
                2 -> {
                    android.util.Log.d("MainTabsActivity", "Creating ProfileFragment")
                    ProfileFragment()
                }
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
