package com.example.orgware.fusedlocationproviderclient_kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import com.example.orgware.fusedlocationproviderclient_kotlin.helper.LocationHelper
import com.example.orgware.fusedlocationproviderclient_kotlin.helper.LocationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapActivity : FragmentActivity(), OnMapReadyCallback, LocationManager {

    private var locationHelper: LocationHelper? = null
    private var lastLocation: Location? = null
    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private var latLng: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkLocationPermission()
        locationHelper = LocationHelper(this@MapActivity, this)
        locationHelper!!.startLocationUpdates()

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)

        val success = googleMap!!.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map
            )
        )
        updateViews()
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            lastLocation = location
        }

    }

    override fun getLastKnownLocation(location: Location?) {
        if (lastLocation != null) {
            this.lastLocation = lastLocation
            if (mapFragment != null) {
                mapFragment!!.getMapAsync(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (locationHelper != null) {
            locationHelper!!.startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        if (locationHelper != null) {
            locationHelper!!.stopLocationUpdates()
        }
    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )


            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            return false
        } else {
            return true
        }
    }


    @SuppressLint("MissingPermission")
    private fun updateViews() {
        if (lastLocation != null) {
            mMap!!.clear()
            mMap!!.isMyLocationEnabled = true

            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
                .zoom(12f)
                .build()
            val options = MarkerOptions().position(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            val m = mMap!!.addMarker(options)
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            latLng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
//            setMarker(R.drawable.usermarker, LatLng(lastLocation!!.getLatitude(), lastLocation!!.getLongitude()))
        }
    }

    fun setMarker(marker: Int, latLng: LatLng) {
        mMap!!.addMarker(
            MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(marker)).position(
                latLng
            )
        )
    }
}