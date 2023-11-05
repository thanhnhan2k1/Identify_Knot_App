package com.example.identifyknotapp.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Output(
    @SerialName("averageAreaDouble")
    val averageAreaDouble: Double? = 0.0,
    @SerialName("averageAreaSingle")
    val averageAreaSingle: Double? = 0.0,
    @SerialName("numberOfDouble")
    val numberOfDouble: Int? = 0,
    @SerialName("numberOfSingle")
    val numberOfSingle: Int? = 0,
    @SerialName("resultImage")
    val resultImage: String? = ""
)