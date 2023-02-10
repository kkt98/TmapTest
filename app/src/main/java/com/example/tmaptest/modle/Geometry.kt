package com.example.tmaptest.modle


import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("coordinates")
    val coordinates: MutableList<Any>,
    @SerializedName("type")
    val type: String
)