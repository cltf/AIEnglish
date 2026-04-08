package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

object ReadingSkillsRepository {
    private val gson = Gson()

    fun load(context: Context): ReadingSkillsFile {
        context.assets.open("reading_skills_zhongkao.json").use { input ->
            return gson.fromJson(InputStreamReader(input, Charsets.UTF_8), ReadingSkillsFile::class.java)
        }
    }
}
