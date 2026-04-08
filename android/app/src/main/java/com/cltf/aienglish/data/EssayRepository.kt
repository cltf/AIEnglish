package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class EssayRepository(context: Context) {

    private val appContext = context.applicationContext
    private val gson = Gson()

    @Volatile
    private var cached: List<EssayExam> = emptyList()

    suspend fun load(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            appContext.assets.open("essays.json").use { input ->
                val file = gson.fromJson(InputStreamReader(input, Charsets.UTF_8), EssaysFile::class.java)
                cached = file.exams
            }
        }
    }

    fun exams(): List<EssayExam> = cached
}
