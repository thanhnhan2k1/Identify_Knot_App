package com.example.identifyknotapp.data

import com.example.identifyknotapp.data.model.WoodRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteService {
    @POST("uploads")
    fun segmentationImage(@Query("image") image: String): Call<Any>
    @POST("predictNhan")
    fun getWoodDescriptions(@Body image: WoodRequestBody): Call<Any>
}