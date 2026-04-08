package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

object ReadingHighFreqRepository {
    private val gson = Gson()

    fun load(context: Context): ReadingHighFreqFile {
        context.assets.open("reading_high_freq.json").use { input ->
            return gson.fromJson(InputStreamReader(input, Charsets.UTF_8), ReadingHighFreqFile::class.java)
        }
    }
}
