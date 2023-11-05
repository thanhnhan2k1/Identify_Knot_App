package com.example.identifyknotapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.identifyknotapp.data.RemoteService
import com.example.identifyknotapp.data.SegmentationRepository

class SegmentationViewModelFactory(private val remoteService: RemoteService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SegmentationViewModel::class.java)) {
            return SegmentationViewModel(remoteService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}