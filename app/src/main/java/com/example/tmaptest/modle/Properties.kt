package com.example.tmaptest.modle


import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("categoryRoadType")
    val categoryRoadType: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("direction")
    val direction: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("facilityName")
    val facilityName: String,
    @SerializedName("facilityType")
    val facilityType: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("intersectionName")
    val intersectionName: String,
    @SerializedName("lineIndex")
    val lineIndex: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("nearPoiName")
    val nearPoiName: String,
    @SerializedName("nearPoiX")
    val nearPoiX: String,
    @SerializedName("nearPoiY")
    val nearPoiY: String,
    @SerializedName("pointIndex")
    val pointIndex: Int,
    @SerializedName("pointType")
    val pointType: String,
    @SerializedName("roadType")
    val roadType: Int,
    @SerializedName("time")
    val time: Int,
    @SerializedName("totalDistance")
    val totalDistance: Int,
    @SerializedName("totalTime")
    val totalTime: Int,
    @SerializedName("turnType")
    val turnType: Int
)