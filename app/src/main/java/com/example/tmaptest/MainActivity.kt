package com.example.tmaptest

import android.Manifest
import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tmaptest.databinding.ActivityMainBinding
import com.example.tmaptest.modle.DataClass
import com.example.tmaptest.modle.Feature
import com.example.tmaptest.network.RetrofitHelper
import com.example.tmaptest.network.RetrofitService
import com.example.tmaptest.utill.Constants
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.skt.Tmap.*
import com.skt.Tmap.TMapRenderer.TILETYPE_ENGLISHTILE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URLEncoder
import java.util.*


class MainActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    lateinit var binding: ActivityMainBinding
    var tMapView: TMapView? = null

    var Latitude: Double? = null
    var Longitude: Double? = null

    var geoLat: Double? = null
    var geoLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tedPermission()

        val sampleTmap = binding.SampleTmap
        tMapView = TMapView(this)

        tMapView?.setSKTMapApiKey("cfBsH5K5B2C0cde6c6Cdaq1VOAWQ4hL3CtCsq7q7")

        tMapView?.setTrackingMode(true) //위치추적 모드
        tMapView?.setSightVisible(true) //
        tMapView?.setIconVisibility(true) //현재위치 아이콘표시
        tMapView?.TileType = TILETYPE_ENGLISHTILE
        sampleTmap.addView(tMapView)
        val tmapgps = TMapGpsManager(this)
        tmapgps.minTime = 1000
        tmapgps.minDistance = 5F

        tmapgps.OpenGps()

        binding.searchView.setOnKeyListener { view, keyCode, event ->
            // Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER
            ) {

                tMapView!!.removeTMapPolyLine("Line1")

                Log.i("abcd", "$Latitude, $Longitude, $geoLat, $geoLng")

                // 키패드 내리기
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)

                val geocoder = Geocoder(this, Locale.KOREA)

                try {
                    val addressList = geocoder.getFromLocationName(binding.searchView.text.toString(), 3) //최대 3개
                    val buffer = StringBuffer()
                    for (address in addressList!!) {
                        val lat = address.latitude //위도
                        val lng = address.longitude //경도
                        buffer.append("$lat , $lng\n")
                    }
                    geoLat = addressList[0].latitude
                    geoLng = addressList[0].longitude
                    android.app.AlertDialog.Builder(this).setMessage(buffer.toString()).create()
                        .show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                Toast.makeText(this, "${binding.searchView.text}", Toast.LENGTH_SHORT).show()

                startRetrofit()

//                val marker = TMapMarkerItem()
//                marker.id = "maker1"
//                marker.setPosition(Latitude!!.toFloat(), Longitude!!.toFloat())
//                marker.icon = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_myplaces)
//                tMapView!!.addMarkerItem("marker1", marker)

                true
            }

            false
        }

        //지오코딩 작업을 수행하는 객체 생성
    }

    var features: MutableList<Feature> = ArrayList()
    var s: String? = null

    private fun startRetrofit() {

        val START_ENCODE = URLEncoder.encode(Constants.START, "UTF-8")
        val END_ENCODE = URLEncoder.encode(Constants.END, "UTF-8")

        val call = RetrofitHelper.getRetrofitInstance("https://apis.openapi.sk.com/")
        call.create(RetrofitService::class.java).directions(
            Longitude?.toFloat(),
            Latitude?.toFloat(),
            geoLng?.toFloat(),
            geoLat?.toFloat(),
            START_ENCODE,
            END_ENCODE,
            0.toString()
        ).enqueue(object : Callback<DataClass> {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {

                val builder = AlertDialog.Builder(this@MainActivity)

                val aaaa: MutableList<Any> = mutableListOf()

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
                tMapPolyLine.lineWidth = 1f

                var marker_lat: String? = null
                var marker_lng: String? = null
                list.forEach {location ->

                    tMapPolyLine.addLinePoint(TMapPoint(location[1].toDouble(), location[0].toDouble()))

                    marker_lat = location[1]
                    marker_lng = location[0]
                }

                val markerItem1 = TMapMarkerItem()
                val tMapPoint1 = TMapPoint(marker_lat!!.toDouble(), marker_lng!!.toDouble()) // SKT타워
                val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_myplaces)

                markerItem1.icon = bitmap // 마커 아이콘 지정
                markerItem1.setPosition(0.5f, 1.0f) // 마커의 중심점을 중앙, 하단으로 설정
                markerItem1.tMapPoint = tMapPoint1 // 마커의 좌표 지정
                markerItem1.name = "SKT타워" // 마커의 타이틀 지정
                tMapView!!.addMarkerItem("markerItem1", markerItem1) // 지도에 마커 추가

                tMapView!!.addTMapPolyLine("Line1", tMapPolyLine)

//                builder.setMessage(list.toString()).show()

            }

            override fun onFailure(call: Call<DataClass>, t: Throwable) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage(t.message).show()
            }

        })

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

//        Log.i("aaaaa", p0?.latitude.toString()+ " " + p0?.longitude)

        Longitude = p0?.longitude
        Latitude = p0?.latitude

        Log.i("aaa", Longitude.toString() + " + " + Latitude.toString())

    }

}




// https://apis.openapi.sk.com/