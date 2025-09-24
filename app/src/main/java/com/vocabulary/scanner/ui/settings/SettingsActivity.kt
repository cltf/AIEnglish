package com.vocabulary.scanner.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vocabulary.scanner.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsAdapter: SettingsAdapter
    private var settingsList = mutableListOf<SettingItem>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupSettingsData()
        setupRecyclerView()
        setupClickListeners()
    }
    
    private fun setupSettingsData() {
        settingsList.clear()
        settingsList.addAll(listOf(
            SettingItem(
                icon = android.R.drawable.ic_menu_search,
                title = "词典接口选择",
                value = "百度词典",
                type = SettingType.DROPDOWN
            ),
            SettingItem(
                icon = android.R.drawable.ic_menu_agenda,
                title = "识别语言",
                value = "英语（默认）",
                type = SettingType.DROPDOWN
            ),
            SettingItem(
                icon = android.R.drawable.ic_menu_edit,
                title = "字体大小",
                value = "标准",
                type = SettingType.DROPDOWN
            ),
            SettingItem(
                icon = android.R.drawable.ic_menu_delete,
                title = "缓存清理",
                value = "清理",
                type = SettingType.ACTION
            ),
            SettingItem(
                icon = android.R.drawable.ic_menu_info_details,
                title = "版本更新",
                value = "V1.0.0（最新）",
                type = SettingType.INFO
            )
        ))
    }
    
    private fun setupRecyclerView() {
        settingsAdapter = SettingsAdapter(settingsList) { position ->
            handleSettingClick(position)
        }
        
        binding.recyclerSettings.layoutManager = LinearLayoutManager(this)
        binding.recyclerSettings.adapter = settingsAdapter
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun handleSettingClick(position: Int) {
        val item = settingsList[position]
        
        when (item.type) {
            SettingType.DROPDOWN -> showDropdownDialog(item, position)
            SettingType.ACTION -> handleAction(item)
            SettingType.INFO -> handleInfo(item)
        }
    }
    
    private fun showDropdownDialog(item: SettingItem, position: Int) {
        val options = when (item.title) {
            "词典接口选择" -> arrayOf("百度词典", "网易有道词典")
            "识别语言" -> arrayOf("英语（默认）")
            "字体大小" -> arrayOf("小", "标准", "大", "超大")
            else -> arrayOf("选项1", "选项2", "选项3")
        }
        
        AlertDialog.Builder(this)
            .setTitle(item.title)
            .setItems(options) { _, which ->
                val selectedValue = options[which]
                settingsList[position] = item.copy(value = selectedValue)
                settingsAdapter.notifyItemChanged(position)
            }
            .show()
    }
    
    private fun handleAction(item: SettingItem) {
        when (item.title) {
            "缓存清理" -> showClearCacheDialog()
        }
    }
    
    private fun handleInfo(item: SettingItem) {
        when (item.title) {
            "版本更新" -> {
                // 检查是否有更新
                Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showClearCacheDialog() {
        AlertDialog.Builder(this)
            .setTitle("确认清理")
            .setMessage("确定清理缓存吗？（清理后不影响生词本数据）")
            .setPositiveButton("确定") { _, _ ->
                // 模拟清理缓存
                Toast.makeText(this, "清理完成", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}

// 数据类
data class SettingItem(
    val icon: Int,
    val title: String,
    val value: String,
    val type: SettingType
)

enum class SettingType {
    DROPDOWN,
    ACTION,
    INFO
}

// RecyclerView适配器
class SettingsAdapter(
    private val items: List<SettingItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(android.R.id.icon)
        val tvTitle: TextView = view.findViewById(android.R.id.title)
        val tvValue: TextView = view.findViewById(android.R.id.summary)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        holder.ivIcon.setImageResource(item.icon)
        holder.tvTitle.text = item.title
        holder.tvValue.text = item.value
        
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }
    
    override fun getItemCount() = items.size
}





