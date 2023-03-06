package com.example.tmaptest.network

import com.example.tmaptest.modle.DataClass
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
        @Query("searchOption") searchOption:String,
    ): Call<DataClass>

    @GET("getVilageFcst?serviceKey=H7PvoIiO2D6%2BqVfe6kF2WAoJgdpbVUtJT52Wx7dL6%2BDLP4IEk5i5xqP%2BGZMDktix9xaYS03X6YP4JtLGSnuunw%3D%3D")
    fun getWeather(
        @Query("pageNo") pageNo : Int,
        @Query("numOfRows") numOfRows : Int,
        @Query("dataType") dataType : String,
        @Query("base_date") baseDate : Int,
        @Query("base_time") baseTime : Int,
        @Query("nx") nx : Int,
        @Query("ny") ny : Int,
    ) : Call<com.example.tmaptest.weathermodle.WeatherDataClass>

}



