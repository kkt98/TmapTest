package com.example.tmaptest.network

import com.example.tmaptest.modle.DataClass
import com.example.tmaptest.modle.Geometry
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    @Headers(
        "accept: application/json",
        "content-type: application/json",
        "appkey: cfBsH5K5B2C0cde6c6Cdaq1VOAWQ4hL3CtCsq7q7"
    )
    @GET("tmap/routes/pedestrian?version=1&")
    fun directions(
        @Query("startX") startX: Float?,
        @Query("startY") startY: Float?,
        @Query("endX") endX: Float?,
        @Query("endY") endY: Float?,
        @Query("startName") startName: String,
        @Query("endName") endName: String,
        @Query("searchOption") searchOption:String
    ): Call<DataClass>

}

//@Body startX: Double?, @Body startY:Double?, @Body endX:Double?, @Body endY:Double?, @Body startName:String, @Body endName:String


