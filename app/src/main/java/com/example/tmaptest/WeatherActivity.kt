package com.example.tmaptest


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.tmaptest.databinding.ActivityWeatherBinding
import com.example.tmaptest.network.RetrofitHelper
import com.example.tmaptest.network.RetrofitService
import com.example.tmaptest.weathermodle.WeatherDataClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.round

class WeatherActivity : AppCompatActivity() {

    lateinit var binding : ActivityWeatherBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startWeatherRetrofit()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startWeatherRetrofit() {

        val builder = AlertDialog.Builder(this@WeatherActivity)

        val time:LocalDateTime = LocalDateTime.now().minusMinutes(30)

        val call = RetrofitHelper.getRetrofitInstance("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
        call.create(RetrofitService::class.java).getWeather(
            1,
            14,
            "JSON",
            time.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt(),
            time.format(DateTimeFormatter.ofPattern("HHmm")).toInt(),
            round(MainActivity.myLocation.Latitude!!).toInt(),
            round(MainActivity.myLocation.Longitude!!).toInt(),

            ).enqueue(object : Callback<WeatherDataClass> {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onResponse(call: Call<WeatherDataClass>, response: Response<WeatherDataClass>) {

                builder.setMessage(response.body().toString()).show()

                Log.i("aaa", time.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                Log.i("aaa", time.format(DateTimeFormatter.ofPattern("HHmm")))
                Log.i("aaa", round(MainActivity.myLocation.Latitude!!).toString())
                Log.i("aaa", round(MainActivity.myLocation.Longitude!!).toString())

//                Toast.makeText(this@WeatherActivity, "${response.body()}", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<WeatherDataClass>, t: Throwable) {
                builder.setMessage(t.message).show()
            }

        })

    }
}