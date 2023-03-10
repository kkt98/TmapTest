package com.example.tmaptest

import android.Manifest
import android.R
import android.content.Intent
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
import com.example.tmaptest.MainActivity.myLocation.Latitude
import com.example.tmaptest.MainActivity.myLocation.Longitude
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

    object myLocation {

        var Latitude: Double? = null
        var Longitude: Double? = null

    }

    var geoLat: Double? = null
    var geoLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tedPermission()

        binding.weatherBtn.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
        }

        val sampleTmap = binding.SampleTmap
        tMapView = TMapView(this)

        tMapView?.setSKTMapApiKey("cfBsH5K5B2C0cde6c6Cdaq1VOAWQ4hL3CtCsq7q7")

        tMapView?.setTrackingMode(true) //???????????? ??????
        tMapView?.setSightVisible(true) //
        tMapView?.setIconVisibility(true) //???????????? ???????????????
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

                // ????????? ?????????
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)

                val geocoder = Geocoder(this, Locale.KOREA)

                try {
                    val addressList = geocoder.getFromLocationName(binding.searchView.text.toString(), 3) //?????? 3???
                    val buffer = StringBuffer()
                    for (address in addressList!!) {
                        val lat = address.latitude //??????
                        val lng = address.longitude //??????
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

                true
            }

            false
        }

        //???????????? ????????? ???????????? ?????? ??????
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
                val tMapPoint1 = TMapPoint(marker_lat!!.toDouble(), marker_lng!!.toDouble())
                val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_myplaces)

                markerItem1.icon = bitmap // ?????? ????????? ??????
                markerItem1.setPosition(0.5f, 1.0f) // ????????? ???????????? ??????, ???????????? ??????
                markerItem1.tMapPoint = tMapPoint1 // ????????? ?????? ??????
                markerItem1.name = "SKT??????" // ????????? ????????? ??????
                tMapView!!.addMarkerItem("markerItem1", markerItem1) // ????????? ?????? ??????

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

            //????????? ???????????? ???
            override fun onPermissionGranted() {

                Toast.makeText(this@MainActivity, "???????????? ????????????", Toast.LENGTH_SHORT).show()

            }

            //????????? ???????????? ???
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "????????? ??????????????????", Toast.LENGTH_SHORT).show()
            }
        })

            .setDeniedMessage("????????? ????????? ?????????")// ????????? ?????? ??? ???????????? Dialog Message
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)// ???????????? ??????(????????? ??????)
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