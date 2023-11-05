package com.example.identifyknotapp.data

import com.example.identifyknotapp.data.model.Output
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteService {
    @POST("uploads")
    fun segmentationImage(@Query("image") image: String): Call<Any>
}