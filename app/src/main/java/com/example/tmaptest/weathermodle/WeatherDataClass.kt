package com.example.tmaptest.weathermodle


import com.google.gson.annotations.SerializedName

data class WeatherDataClass(
    @SerializedName("response")
    val response: Response
)