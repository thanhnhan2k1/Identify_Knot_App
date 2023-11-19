package com.example.identifyknotapp.ui

import android.util.Log
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
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class SegmentationViewModel(
    private val remote: RemoteService,
    private val remoteDescription: RemoteService
) : ViewModel() {
    private val _segmentationResult = MutableLiveData<Output?>()
    private val _woodDescriptions = MutableLiveData<List<WoodDescription>>()
    private val _loadingSuccess = MutableLiveData<Boolean>()
    val segmentationResult: LiveData<Output?> = _segmentationResult
    val woodDescriptions: LiveData<List<WoodDescription>> = _woodDescriptions
    val loadingSuccess: LiveData<Boolean> = _loadingSuccess

    fun predictSegmentationImage(imageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val output = remote.segmentationImage(imageName)
            val id = imageName.split(".").first()
            val db = Firebase.database.reference.child("history")
            output.enqueue(object : Callback,
                retrofit2.Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.body() != null) {
                        val map = response.body() as LinkedTreeMap<*, *>
                        val result = Output(
                            numberOfSingle = map["numberOfSingle"].toString().toDouble().toInt(),
                            numberOfDouble = map["numberOfDouble"].toString().toDouble().toInt(),
                            averageAreaSingle = map["averageAreaSingle"].toString()
                                .toDoubleOrNull(),
                            averageAreaDouble = map["averageAreaDouble"].toString()
                                .toDoubleOrNull(),
                            resultImage = map["resultImage"].toString()
                        )
                        val data = HashMap<String, Any>()
                        data["numberOfSingle"] = result.numberOfSingle ?: 0
                        data["numberOfDouble"] = result.numberOfDouble ?: 0
                        data["averageAreaSingle"] = result.averageAreaSingle ?: 0.0
                        data["averageAreaDouble"] = result.averageAreaDouble ?: 0.0
                        data["mask_link"] = result.resultImage ?: ""
                        data["name"] = "N/A"

                        db.child(id).updateChildren(data)
                        _segmentationResult.postValue(result)
                        _loadingSuccess.postValue(true)
                    } else {
                        _segmentationResult.postValue(null)
                        _loadingSuccess.postValue(false)
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.i("Response", "Failed")
                    _loadingSuccess.postValue(false)
                }
            })
        }
    }

    fun getWoodDescriptions(image: WoodRequestBody, imageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val output = remoteDescription.getWoodDescriptions(image)
            val id = imageName.split(".").first()
            val db = Firebase.database.reference.child("history")
            output.enqueue(object : Callback,
                retrofit2.Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.body() != null) {
                        val map = response.body() as LinkedTreeMap<*, *>
                        if (map["des"].toString() == "OK") {
                            val woodResponse = WoodResponse(
                                des = map["des"].toString(),
                                id = map["id"].toString(),
                                vietnamName = map["vietnamName"].toString(),
                                scientificName = map["scientificName"].toString(),
                                commercialName = map["commercialName"].toString(),
                                categoryWood = map["categoryWood"].toString(),
                                preservation = map["preservation"].toString(),
                                family = map["family"].toString(),
                                specificGravity = map["specificGravity"].toString(),
                                characteristic = map["characteristic"].toString(),
                                appendixCites = map["appendixCites"].toString(),
                                note = map["note"].toString(),
                                area = map["area"].toString(),
                                color = map["color"].toString(),
                                prob = map["prob"].toString()
                            )

                            val data = HashMap<String, Any>()
                            data["name"] = woodResponse.vietnamName
                            data["vietnamName"] = woodResponse.vietnamName
                            data["scientificName"] = woodResponse.scientificName
                            data["commercialName"] = woodResponse.commercialName
                            data["categoryWood"] = woodResponse.categoryWood
                            data["preservation"] = woodResponse.preservation
                            data["family"] = woodResponse.family
                            data["specificGravity"] = woodResponse.specificGravity
                            data["characteristic"] = woodResponse.characteristic
                            data["appendixCites"] = woodResponse.appendixCites
                            data["note"] = woodResponse.note
                            data["area"] = woodResponse.area
                            data["color"] = woodResponse.color
                            data["prob"] = woodResponse.prob

                            db.child(id).updateChildren(data)
                            _woodDescriptions.postValue(woodResponse.toListWoodDescriptions())
                        } else {
                            _woodDescriptions.postValue(listOf())
                        }
                        _loadingSuccess.postValue(true)
                    } else {
                        _woodDescriptions.postValue(listOf())
                        _loadingSuccess.postValue(false)
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.i("Response", "Failed")
                    _loadingSuccess.postValue(false)
                }
            })
        }
    }
}