package com.example.orgware.fusedlocationproviderclient_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.example.orgware.fusedlocationproviderclient_kotlin.helper.LocationHelper
import com.example.orgware.fusedlocationproviderclient_kotlin.helper.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MainActivity : FragmentActivity(), OnMapReadyCallback {

    private var locationHelper: LocationHelper? = null
    private var lastLocation: Location? = null
    private val M_PERMISSIONS_LOCATION = 99
    private var activity: MainActivity? = null
    private val LocationManager: LocationManager? = null
    private var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var latLng: LatLng? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_LOCATION_RUNTIME_PERMISSION = 1
    private val REQ_CODE_LOCATION_SELECTION = 1231

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askRuntimePermissionForLocation()

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap!!.isMyLocationEnabled = true
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

                fusedLocationProviderClient!!.lastLocation
                    .addOnSuccessListener(this) { location: Location? ->
                        val cameraPosition = CameraPosition.Builder()
                            .target(LatLng(location!!.latitude, location!!.longitude))
                            .zoom(12f)
                            .build()
                        val options = MarkerOptions().position(LatLng(location!!.latitude, location!!.longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        val m = mMap!!.addMarker(options)
                        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        latLng = LatLng(location!!.latitude, location!!.longitude)

                    }
                    .addOnFailureListener(this) { }


            } else {
                askRuntimePermissionForLocation()
            }
        } else {
            mMap!!.isMyLocationEnabled = true
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

    private fun askRuntimePermissionForLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var allPermissionsGranted = true
            var i = 0
            val mPermissionLength = M_PERMISSIONS_LOCATION
            while (i < mPermissionLength) {
                val permission = M_PERMISSIONS_LOCATION
                if (ActivityCompat.checkSelfPermission(this, permission.toString()) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
                i++
            }
            if (!allPermissionsGranted) {
//                ActivityCompat.requestPermissions(this,M_PERMISSIONS_LOCATION, REQUEST_LOCATION_RUNTIME_PERMISSION)
            } else
                permissionGranted()
        } else
            permissionGranted()
    }

    private fun permissionGranted() {
        Handler().postDelayed(mThread, 500)
    }

    var mThread: Runnable =
        Runnable {
            startActivityForResult(
                Intent(this, MainActivity::class.java),
                REQ_CODE_LOCATION_SELECTION
            )
        }

    private fun permissionDenied() {
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_RUNTIME_PERMISSION) {
            var allPermissionGranted = true
            if (grantResults.size == permissions.size) {
                for (i in permissions.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false
                        break
                    }
                }
            } else {
                allPermissionGranted = false
            }

            if (allPermissionGranted)
                permissionGranted()
            else
                permissionDenied()
        }
    }
}
