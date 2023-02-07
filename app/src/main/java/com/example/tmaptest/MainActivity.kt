package com.example.tmaptest

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tmaptest.databinding.ActivityMainBinding
import com.example.tmaptest.modle.DataClass
import com.example.tmaptest.network.RetrofitHelper
import com.example.tmaptest.network.RetrofitService
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.util.HashMap


class MainActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    lateinit var binding : ActivityMainBinding
    var tMapView: TMapView? = null
    var tMapGps: TMapGpsManager? = null

    var Latitude: Double? = null
    var Longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sampleTmap = binding.SampleTmap
        tMapView = TMapView(this)

        tMapView?.setSKTMapApiKey("cfBsH5K5B2C0cde6c6Cdaq1VOAWQ4hL3CtCsq7q7")
        sampleTmap.addView(tMapView)

        tMapView?.setTrackingMode(true) //위치추적 모드
        tMapView?.setSightVisible(true) //
        tMapView?.setIconVisibility(true) //현재위치 아이콘표시

        val tmapgps = TMapGpsManager(this)
        tmapgps.minTime = 1000
        tmapgps.minDistance = 5F

        tmapgps.OpenGps()

//        tMapView?.setCenterPoint(126.9784147, 37.5666805) //지도의 중심좌표를 이동
//        tMapView?.setLocationPoint(126.9784147, 37.5666805) //현재 위치로 표시될 좌표

        val tpoint: TMapPoint = tMapView!!.locationPoint // 현재위치로 표시되는 좌표의 위도, 경도를 반환합니다
        Latitude = tpoint.latitude
        Longitude = tpoint.longitude

        Log.i("aaa" , "$Latitude $Longitude")

        startRetrofit()

        tedPermission()

    }

    private fun startRetrofit(){

        val START = "출발"
        val END = "도착"

        val START_ENCODE = URLEncoder.encode(START, "UTF-8")
        val END_ENCODE = URLEncoder.encode(END, "UTF-8")

        val dataPart = HashMap<String, String>()
        dataPart.put("email", email)
        dataPart.put("message", message)
        dataPart.put("date", date)

        val call = RetrofitHelper.getRetrofitInstance("https://apis.openapi.sk.com/")
        call.create(RetrofitService::class.java).directions(Longitude, Latitude, 126.9784147, 37.5666805, START_ENCODE, END_ENCODE).enqueue(object: Callback<DataClass> {
            override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {

                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage(response.body().toString()).show()

            }

            override fun onFailure(call: Call<DataClass>, t: Throwable) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage(t.message).show()
            }

        })

    }

    private fun tedPermission() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {

                //권한이 허용됐을 때
                override fun onPermissionGranted() {

                    Toast.makeText(this@MainActivity, "위치정보 사용가능", Toast.LENGTH_SHORT).show()
                    
                }

                //권한이 거부됐을 때
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@MainActivity, "권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                }
            })
            .setDeniedMessage("권한을 허용해 주세요")// 권한이 없을 때 띄워주는 Dialog Message
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)// 얻으려는 권한(여러개 가능)
            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
            .setPermissions(Manifest.permission.INTERNET)
            .check()
    }

    override fun onLocationChange(p0: Location?) {
        tMapView?.setLocationPoint(p0?.longitude ?: 37.5666805, p0?.latitude ?: 126.9784147)
    }
}

// https://apis.openapi.sk.com/