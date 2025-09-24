package com.vocabulary.scanner.ui.vocabulary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vocabulary.scanner.R
import com.vocabulary.scanner.databinding.ActivityVocabularyBinding
import com.vocabulary.scanner.ui.word.WordDetailActivity
import java.text.SimpleDateFormat
import java.util.*

class VocabularyActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVocabularyBinding
    private lateinit var vocabularyAdapter: VocabularyAdapter
    private var vocabularyList = mutableListOf<VocabularyListItem>()
    private var isBatchMode = false
    private var selectedItems = mutableSetOf<Int>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVocabularyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        loadVocabularyData()
        setupClickListeners()
    }
    
    private fun setupRecyclerView() {
        vocabularyAdapter = VocabularyAdapter(vocabularyList) { position ->
            if (isBatchMode) {
                toggleItemSelection(position)
            } else {
                openWordDetail(position)
            }
        }
        
        binding.recyclerVocabulary.layoutManager = LinearLayoutManager(this)
        binding.recyclerVocabulary.adapter = vocabularyAdapter
    }
    
    private fun loadVocabularyData() {
        // 模拟生词本数据，实际应用中应该从数据库获取
        vocabularyList.clear()
        vocabularyList.addAll(listOf(
            VocabularyListItem("abandon", "2024-06-01"),
            VocabularyListItem("beautiful", "2024-06-02"),
            VocabularyListItem("important", "2024-06-03"),
            VocabularyListItem("education", "2024-06-04"),
            VocabularyListItem("knowledge", "2024-06-05")
        ))
        vocabularyAdapter.notifyDataSetChanged()
    }
    
    private fun setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // 批量操作按钮
        binding.btnBatchOperation.setOnClickListener {
            toggleBatchMode()
        }
        
        // 排序下拉菜单
        binding.spinnerSort.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> sortByAddTime()
                    1 -> sortByAlphabet()
                }
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
        
        // 复习按钮
        binding.btnReview.setOnClickListener {
            startReview()
        }
        
        // 批量删除按钮
        binding.btnDeleteSelected.setOnClickListener {
            deleteSelectedItems()
        }
    }
    
    private fun toggleBatchMode() {
        isBatchMode = !isBatchMode
        selectedItems.clear()
        
        if (isBatchMode) {
            binding.btnBatchOperation.text = "取消"
            binding.btnDeleteSelected.visibility = View.VISIBLE
        } else {
            binding.btnBatchOperation.text = "批量操作"
            binding.btnDeleteSelected.visibility = View.GONE
        }
        
        vocabularyAdapter.isBatchMode = isBatchMode
        vocabularyAdapter.selectedItems = selectedItems
        vocabularyAdapter.notifyDataSetChanged()
    }
    
    private fun toggleItemSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }
        vocabularyAdapter.selectedItems = selectedItems
        vocabularyAdapter.notifyItemChanged(position)
        updateDeleteButtonText()
    }
    
    private fun updateDeleteButtonText() {
        val count = selectedItems.size
        binding.btnDeleteSelected.text = "删除所选 ($count)"
        binding.btnDeleteSelected.isEnabled = count > 0
    }
    
    private fun openWordDetail(position: Int) {
        val item = vocabularyList[position]
        val intent = Intent(this, WordDetailActivity::class.java)
        intent.putExtra("word", item.word)
        startActivity(intent)
    }
    
    private fun sortByAddTime() {
        vocabularyList.sortByDescending { item ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(item.addTime)?.time ?: 0L
        }
        vocabularyAdapter.notifyDataSetChanged()
    }
    
    private fun sortByAlphabet() {
        vocabularyList.sortBy { it.word.lowercase() }
        vocabularyAdapter.notifyDataSetChanged()
    }
    
    private fun startReview() {
        if (vocabularyList.isEmpty()) {
            Toast.makeText(this, "生词本为空", Toast.LENGTH_SHORT).show()
            return
        }
        
        // TODO: 实现复习功能
        Toast.makeText(this, "复习功能待实现", Toast.LENGTH_SHORT).show()
    }
    
    private fun deleteSelectedItems() {
        if (selectedItems.isEmpty()) return
        
        val sortedPositions = selectedItems.sortedDescending()
        for (position in sortedPositions) {
            vocabularyList.removeAt(position)
        }
        
        selectedItems.clear()
        vocabularyAdapter.selectedItems = selectedItems
        vocabularyAdapter.notifyDataSetChanged()
        toggleBatchMode()
        
        Toast.makeText(this, "已删除选中的生词", Toast.LENGTH_SHORT).show()
    }
}

// 数据类 - 使用不同的名称避免冲突
data class VocabularyListItem(
    val word: String,
    val addTime: String
)

// RecyclerView适配器
class VocabularyAdapter(
    private val items: List<VocabularyListItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<VocabularyAdapter.ViewHolder>() {
    
    var isBatchMode = false
    var selectedItems = mutableSetOf<Int>()
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkboxSelect: CheckBox = view.findViewById(R.id.checkbox_select)
        val tvWord: TextView = view.findViewById(R.id.tv_word)
        val tvAddTime: TextView = view.findViewById(R.id.tv_add_time)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vocabulary, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        holder.tvWord.text = item.word
        holder.tvAddTime.text = item.addTime
        
        // 批量模式显示复选框
        if (isBatchMode) {
            holder.checkboxSelect.visibility = View.VISIBLE
            holder.checkboxSelect.isChecked = selectedItems.contains(position)
        } else {
            holder.checkboxSelect.visibility = View.GONE
        }
        
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }
    
    override fun getItemCount() = items.size
}

