package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

object DaofaPastExamsRepository {
    private val gson = Gson()

    fun load(context: Context): DaofaPastExamsFile {
        context.assets.open("daofa_past_exams.json").use { input ->
            return gson.fromJson(InputStreamReader(input, Charsets.UTF_8), DaofaPastExamsFile::class.java)
        }
    }
}
