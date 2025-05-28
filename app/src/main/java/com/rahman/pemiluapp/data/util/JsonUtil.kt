package com.rahman.pemiluapp.data.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rahman.pemiluapp.data.model.VoterDataModel
import java.io.File

object JsonUtil {
    const val IMAGE_DIR = "images"
    private val gson = Gson()

    fun toJsonFile(data: List<VoterDataModel>, file: File) {
        file.writeText(gson.toJson(data))
    }

    fun fromJsonFile(file: File): List<VoterDataModel> {
        val type = object : TypeToken<List<VoterDataModel>>() {}.type
        return gson.fromJson(file.readText(), type)
    }
}