package com.example.identifyknotapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WoodHistory(
    val image_link: String = "",
    val mask_link: String = "",
    val name: String = "",
    val date: String = "",
    val vietnamName: String = "",
    val scientificName: String = "",
    val commercialName: String = "",
    val categoryWood: String = "",
    val preservation: String = "",
    val family: String = "",
    val specificGravity: String = "",
    val characteristic: String = "",
    val appendixCites: String = "",
    val note: String = "",
    val color: String = "",
    val area: String = "",
    val prob: String = "",
    val numberOfSingle: Int = 0,
    val numberOfDouble: Int = 0,
    val averageAreaSingle: Double = 0.0,
    val averageAreaDouble: Double = 0.0
) : Parcelable
