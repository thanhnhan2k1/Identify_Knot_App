package com.example.identifyknotapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.identifyknotapp.data.RemoteService
import com.example.identifyknotapp.data.model.Output
import com.example.identifyknotapp.data.model.WoodDescription
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class SegmentationViewModel(private val remote: RemoteService): ViewModel() {
    private val _segmentationResult = MutableLiveData<Output>()
    private val _woodDescriptions = MutableLiveData<List<WoodDescription>>()
    val segmentationResult: LiveData<Output> = _segmentationResult
    val woodDescriptions: LiveData<List<WoodDescription>> = _woodDescriptions

    fun predictSegmentationImage(imageName: String){
        viewModelScope.launch(Dispatchers.IO){
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
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.i("Response", "Failed")
                }
            })
        }
    }
    fun getWoodDescriptions(){
        viewModelScope.launch {
            _woodDescriptions.postValue(emptyList())
        }
    }
}