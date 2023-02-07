package com.example.tmaptest.network

import com.example.tmaptest.modle.DataClass
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    @Headers("accept: application/json", "content-type: application/json", "appkey: cfBsH5K5B2C0cde6c6Cdaq1VOAWQ4hL3CtCsq7q7")
    @POST("tmap/routes/pedestrian?version=1&")
    fun directions(@PartMap data: Map<String, String>): Call<DataClass>

}

//@Body startX: Double?, @Body startY:Double?, @Body endX:Double?, @Body endY:Double?, @Body startName:String, @Body endName:String