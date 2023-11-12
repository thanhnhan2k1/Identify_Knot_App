package com.example.identifyknotapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WoodRequestBody (
    val image: String
): Parcelable