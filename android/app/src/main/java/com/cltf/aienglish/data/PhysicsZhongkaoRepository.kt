package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

/** 物理 · 中考真题（与数学历年真题 / 语文中考真题 JSON 结构相同） */
object PhysicsZhongkaoRepository {
    private val gson = Gson()

    fun load(context: Context): DaofaPastExamsFile {
        context.assets.open("physics_zhongkao.json").use { input ->
            return gson.fromJson(InputStreamReader(input, Charsets.UTF_8), DaofaPastExamsFile::class.java)
        }
    }
}
