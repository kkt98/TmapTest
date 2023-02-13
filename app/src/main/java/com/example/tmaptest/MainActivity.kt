package com.example.tmaptest

import android.Manifest
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tmaptest.databinding.ActivityMainBinding
import com.example.tmaptest.modle.DataClass
import com.example.tmaptest.modle.Feature
import com.example.tmaptest.network.RetrofitHelper
import com.example.tmaptest.network.RetrofitService
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.skt.Tmap.*
import com.skt.Tmap.TMapRenderer.TILETYPE_ENGLISHTILE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder


class MainActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    lateinit var binding: ActivityMainBinding
    var tMapView: TMapView? = null

    var Latitude: Double? = null
    var Longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tedPermission()

        val sampleTmap = binding.SampleTmap
        tMapView = TMapView(this)

        tMapView?.setSKTMapApiKey("cfBsH5K5B2C0cde6c6Cdaq1VOAWQ4hL3CtCsq7q7")
        //sampleTmap.addView(tMapView)

        tMapView?.setTrackingMode(true) //위치추적 모드
        tMapView?.setSightVisible(true) //
        tMapView?.setIconVisibility(true) //현재위치 아이콘표시
        tMapView?.TileType = TILETYPE_ENGLISHTILE
        sampleTmap.addView(tMapView)
        val tmapgps = TMapGpsManager(this)
        tmapgps.minTime = 1000
        tmapgps.minDistance = 5F

        tmapgps.OpenGps()
        //tMapView?.setCenterPoint(126.9784147, 37.5666805) //지도의 중심좌표를 이동
        //현재 위치로 표시될 좌표

//        val tpoint: TMapPoint = tMapView!!.locationPoint // 현재위치로 표시되는 좌표의 위도, 경도를 반환합니다
//        Latitude = tpoint.latitude
//        Longitude = tpoint.longitude
//
//        tMapView?.setLocationPoint(Latitude!!, Longitude!!)

//        Road()

    }

    var features: MutableList<Feature> = ArrayList()
    var aaaa: MutableList<Any> = mutableListOf()
    var s: String? = null

    private fun startRetrofit(p0: Location?) {

        val START = "출발"
        val END = "도착"

        val START_ENCODE = URLEncoder.encode(START, "UTF-8")
        val END_ENCODE = URLEncoder.encode(END, "UTF-8")

        val call = RetrofitHelper.getRetrofitInstance("https://apis.openapi.sk.com/")
        call.create(RetrofitService::class.java).directions(
            p0?.longitude?.toFloat(),
            p0?.latitude?.toFloat(),
            126.9784147.toFloat(),
            37.5666805.toFloat(),
            START_ENCODE,
            END_ENCODE,
            0.toString()
        ).enqueue(object : Callback<DataClass> {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {

                val builder = AlertDialog.Builder(this@MainActivity)

                features = response.body()!!.features

                features.forEach { item ->

                    s = item.geometry.type

                    if (s == "LineString") {

                        aaaa.add(item.geometry.coordinates)
                    }

                }
                val reg = Regex("\\[*\\]*")

                aaaa.toString().split("[ ]")

                val cccc = aaaa.toString().replace(reg, "")

                val list = cccc.split(',').toList().chunked(2)

                val tMapPolyLine = TMapPolyLine()
                tMapPolyLine.lineColor = Color.BLUE
                tMapPolyLine.lineWidth = 2f

                list.forEach {location ->

                    tMapPolyLine.addLinePoint(TMapPoint(location[1].toDouble(), location[0].toDouble()))


                }


                tMapView!!.addTMapPolyLine("Line1", tMapPolyLine)

//                builder.setMessage(list.toString()).show()

            }

            override fun onFailure(call: Call<DataClass>, t: Throwable) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage(t.message).show()
            }

        })

    }

    private fun Road() {

        val thread = object : Thread() {
            override fun run() {
                Log.i("aab", "$Latitude $Longitude")
                val tMapPointStart = TMapPoint(37.563685889, 126.975584404) // 시청역(출발지)

                val tMapPointEnd = TMapPoint(37.551135, 126.988205) // N서울타워(목적지)


                val tMapPolyLine = TMapData().findPathDataWithType(
                    TMapData.TMapPathType.PEDESTRIAN_PATH,
                    tMapPointStart,
                    tMapPointEnd
                )
                tMapPolyLine.lineColor = 0xFF000000.toInt()
                tMapPolyLine.lineWidth = 2f
                tMapView!!.addTMapPolyLine("Line1", tMapPolyLine)

            }
        }

        thread.start()
    }

    private fun tedPermission() {
        TedPermission.create().setPermissionListener(object : PermissionListener {

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
            .setPermissions(Manifest.permission.INTERNET).check()
    }

    override fun onLocationChange(p0: Location?) {
        tMapView?.setLocationPoint(p0?.longitude ?: 37.5666805, p0?.latitude ?: 126.9784147)

        Log.i("aaaaa", p0?.latitude.toString()+ " " + p0?.longitude)

        startRetrofit(p0)
    }

}


// https://apis.openapi.sk.com/