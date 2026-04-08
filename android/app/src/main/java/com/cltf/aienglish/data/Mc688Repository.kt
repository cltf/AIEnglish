package com.cltf.aienglish.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

object Mc688Repository {
    private val gson = Gson()

    fun load(context: Context): Mc688File {
        context.assets.open("mc688_21day.json").use { input ->
            return gson.fromJson(InputStreamReader(input, Charsets.UTF_8), Mc688File::class.java)
        }
    }
}
