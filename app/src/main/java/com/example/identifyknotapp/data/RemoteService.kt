package com.example.identifyknotapp.data

import com.example.identifyknotapp.data.model.Output
import com.example.identifyknotapp.data.model.WoodDescription
import com.example.identifyknotapp.data.model.WoodRequestBody
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteService {
    @POST("uploads")
    fun segmentationImage(@Query("image") image: String): Call<Any>
    @POST("predict")
    fun getWoodDescriptions(@Body image: WoodRequestBody): Call<String>
}