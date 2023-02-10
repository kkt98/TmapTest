package com.example.tmaptest.modle


import com.google.gson.annotations.SerializedName

data class DataClass(
    @SerializedName("features")
    val features: MutableList<Feature>,
//    @SerializedName("type")
//    val type: String
)