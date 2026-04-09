package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

/** 数学 · 历年真题（与语文中考真题 JSON 结构相同） */
object MathZhongkaoRepository {
    private val gson = Gson()

    fun load(context: Context): DaofaPastExamsFile {
        context.assets.open("math_zhongkao.json").use { input ->
            return gson.fromJson(InputStreamReader(input, Charsets.UTF_8), DaofaPastExamsFile::class.java)
        }
    }
}
