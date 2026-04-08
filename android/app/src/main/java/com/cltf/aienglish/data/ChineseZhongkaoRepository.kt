package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

/** 语文 · 中考真题（与道法历年题 JSON 结构相同） */
object ChineseZhongkaoRepository {
    private val gson = Gson()

    fun load(context: Context): DaofaPastExamsFile {
        context.assets.open("chinese_zhongkao.json").use { input ->
            return gson.fromJson(InputStreamReader(input, Charsets.UTF_8), DaofaPastExamsFile::class.java)
        }
    }
}
