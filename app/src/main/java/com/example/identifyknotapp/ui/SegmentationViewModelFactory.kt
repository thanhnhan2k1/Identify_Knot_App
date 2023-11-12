package com.example.identifyknotapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.identifyknotapp.data.RemoteService

class SegmentationViewModelFactory(
    private val remoteService: RemoteService,
    private val remoteDescription: RemoteService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SegmentationViewModel::class.java)) {
            return SegmentationViewModel(remoteService, remoteDescription) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}