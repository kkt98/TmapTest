package com.example.tmaptest.modle


import com.google.gson.annotations.SerializedName

data class DataClass(
    @SerializedName("features")
    val features: List<Feature>,
    @SerializedName("type")
    val type: String
)