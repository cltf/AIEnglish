package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class ReadingRepository(context: Context) {

    private val appContext = context.applicationContext
    private val gson = Gson()

    @Volatile
    private var cached: List<ReadingSubject> = emptyList()

    suspend fun load(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            appContext.assets.open("reading_content.json").use { input ->
                val file = gson.fromJson(InputStreamReader(input, Charsets.UTF_8), ReadingContentFile::class.java)
                cached = file.subjects
            }
        }
    }

    fun subjects(): List<ReadingSubject> = cached
}
