package com.example.identifyknotapp.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.util.Base64
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.identifyknotapp.data.RemoteService
import com.example.identifyknotapp.data.model.Output
import com.example.identifyknotapp.data.model.WoodDescription
import com.example.identifyknotapp.data.model.WoodRequestBody
import com.example.identifyknotapp.data.model.WoodResponse
import com.example.identifyknotapp.ui.result.toListWoodDescriptions
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import javax.security.auth.callback.Callback

class SegmentationViewModel(private val remote: RemoteService,private val remoteDescription: RemoteService): ViewModel() {
    private val _segmentationResult = MutableLiveData<Output>()
    private val _woodDescriptions = MutableLiveData<List<WoodDescription>>()
    private val _loadingSuccess = MutableLiveData<Boolean>()
    val segmentationResult: LiveData<Output> = _segmentationResult
    val woodDescriptions: LiveData<List<WoodDescription>> = _woodDescriptions
    val loadingSuccess: LiveData<Boolean> = _loadingSuccess

    fun predictSegmentationImage(imageName: String){
        viewModelScope.launch(Dispatchers.IO) {
            val output = remote.segmentationImage(imageName)
            output.enqueue(object : Callback,
                retrofit2.Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    val map = response.body() as LinkedTreeMap<*, *>
                    val result = Output(
                        numberOfSingle = map["numberOfSingle"].toString().toDouble().toInt(),
                        numberOfDouble = map["numberOfDouble"].toString().toDouble().toInt(),
                        averageAreaSingle = map["averageAreaSingle"].toString().toDoubleOrNull(),
                        averageAreaDouble = map["averageAreaDouble"].toString().toDoubleOrNull(),
                        resultImage = map["resultImage"].toString()
                    )
                    _segmentationResult.postValue(result)
                    _loadingSuccess.postValue(true)
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.i("Response", "Failed")
                    _loadingSuccess.postValue(false)
                }
            })
        }
    }
    fun getWoodDescriptions(image: WoodRequestBody){
        viewModelScope.launch(Dispatchers.IO) {
            val output = remoteDescription.getWoodDescriptions(image)
            output.enqueue(object : Callback,
                retrofit2.Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val gson = Gson()
                    val woodResponse = gson.fromJson(response.body(), WoodResponse::class.java)
                    _woodDescriptions.postValue(woodResponse.toListWoodDescriptions())
                    _loadingSuccess.postValue(true)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.i("Response", "Failed")
                    _loadingSuccess.postValue(false)
                }
            })
        }
    }
}