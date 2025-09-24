package com.vocabulary.scanner.ui.vocabulary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vocabulary.scanner.R
import com.vocabulary.scanner.databinding.FragmentVocabularyBinding
import com.vocabulary.scanner.ui.word.WordDetailActivity
import com.vocabulary.scanner.data.UnifiedVocabularyDatabase
import com.vocabulary.scanner.data.VocabularyType

class VocabularyFragment : Fragment() {
    
    private var _binding: FragmentVocabularyBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var vocabularyAdapter: VocabularyListAdapter
    private var vocabularyList = mutableListOf<VocabularyItem>()
    private var filteredList = mutableListOf<VocabularyItem>()
    private var currentFilterType = VocabularyType.ALL
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVocabularyBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearch()
        setupFilter()
        
        // 延迟初始化数据库，避免启动时的权限问题
        try {
            UnifiedVocabularyDatabase.initialize(requireContext())
            loadVocabularyData()
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果数据库初始化失败，显示空列表
            vocabularyList.clear()
            filteredList.clear()
            vocabularyAdapter.notifyDataSetChanged()
        }
    }
    
    private fun setupRecyclerView() {
        vocabularyAdapter = VocabularyListAdapter(filteredList) { position ->
            openWordDetail(position)
        }
        
        binding.recyclerVocabulary.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerVocabulary.adapter = vocabularyAdapter
    }
    
    private fun loadVocabularyData() {
        // 从数据库加载首页词汇数据（中考词汇和超纲词汇）
        vocabularyList.clear()
        vocabularyList.addAll(getHomePageVocabularyData())
        filteredList.clear()
        filteredList.addAll(vocabularyList)
        vocabularyAdapter.notifyDataSetChanged()
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterVocabulary(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setupFilter() {
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }
    }
    
    private fun showFilterDialog() {
        val filterOptions = arrayOf(
            "所有词汇",
            "中考词汇", 
            "超纲词汇"
        )
        
        val currentSelection = when (currentFilterType) {
            VocabularyType.ALL -> 0
            VocabularyType.MIDDLE_SCHOOL -> 1
            VocabularyType.ADVANCED -> 2
        }
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("选择词汇类型")
            .setSingleChoiceItems(filterOptions, currentSelection) { dialog, which ->
                currentFilterType = when (which) {
                    0 -> VocabularyType.ALL
                    1 -> VocabularyType.MIDDLE_SCHOOL
                    2 -> VocabularyType.ADVANCED
                    else -> VocabularyType.ALL
                }
                loadVocabularyData()
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun filterVocabulary(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(vocabularyList)
        } else {
            // 在整个词库中搜索
            val searchResults = UnifiedVocabularyDatabase.searchWords(query)
            filteredList.addAll(searchResults.map { wordDef ->
                VocabularyItem(
                    word = getWordFromDefinition(wordDef),
                    meaning = wordDef.definitions.firstOrNull()?.meaning ?: ""
                )
            })
        }
        vocabularyAdapter.notifyDataSetChanged()
    }
    
    private fun openWordDetail(position: Int) {
        val item = filteredList[position]
        val intent = Intent(requireContext(), WordDetailActivity::class.java)
        intent.putExtra("word", item.word)
        startActivity(intent)
    }
    
    private fun getHomePageVocabularyData(): List<VocabularyItem> {
        // 根据当前筛选类型获取词汇数据
        val wordDefinitions = when (currentFilterType) {
            VocabularyType.ALL -> UnifiedVocabularyDatabase.getAllWords()
            VocabularyType.MIDDLE_SCHOOL -> UnifiedVocabularyDatabase.getWordsByType(VocabularyType.MIDDLE_SCHOOL)
            VocabularyType.ADVANCED -> UnifiedVocabularyDatabase.getWordsByType(VocabularyType.ADVANCED)
        }
        
        return wordDefinitions.map { wordDef ->
            VocabularyItem(
                word = getWordFromDefinition(wordDef),
                meaning = wordDef.definitions.firstOrNull()?.meaning ?: ""
            )
        }
    }
    
    private fun getWordFromDefinition(wordDef: com.vocabulary.scanner.data.WordDefinition): String {
        return wordDef.word
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// 词汇数据类
data class VocabularyItem(
    val word: String,
    val meaning: String
)

// 词汇列表适配器
class VocabularyListAdapter(
    private val items: List<VocabularyItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<VocabularyListAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWord: TextView = view.findViewById(R.id.tv_word)
        val tvMeaning: TextView = view.findViewById(R.id.tv_meaning)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vocabulary_list, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        holder.tvWord.text = item.word
        holder.tvMeaning.text = item.meaning
        
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }
    
    override fun getItemCount() = items.size
}

