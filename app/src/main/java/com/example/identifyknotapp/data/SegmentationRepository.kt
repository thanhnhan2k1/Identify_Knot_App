package com.example.identifyknotapp.data

import android.util.Log
import com.example.identifyknotapp.data.model.Output
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class SegmentationRepository(private val remote: RemoteService) {
//    fun getResultSegmentation(imageName: String): Flow<Any> = flow {
//        emit(remote.segmentationImage(imageName))
//    } .catch {
//        emit("")
//        Log.d("Response", it.message.toString())
//    }
}