package com.example.orgware.fusedlocationproviderclient_kotlin.helper

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.*

class LocationHelper(var activity: Activity, var locationManager: LocationManager) {
    private val INTERVAL = 20000
    private val FAST_INTERVAL = 5000
    private val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

init {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        }
        createLocationRequest()
        createLocationCallBack()
}

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.setInterval(INTERVAL.toLong())
        locationRequest!!.setFastestInterval(FAST_INTERVAL.toLong())
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun createLocationCallBack() {
        if (locationCallback == null) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    if (locationResult != null) {
                        for (location in locationResult.locations) {
                            if (location != null && locationManager != null) {
                                if (locationManager != null) {
                                    locationManager!!.onLocationChanged(location)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this!!.activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this!!.activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this!!.activity!!, PERMISSIONS, 1234)
            return
        }

        fusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
        getLastKnownLocation()
    }

    private fun getLastKnownLocation() {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this!!.activity!!)

        }
        if (ActivityCompat.checkSelfPermission(
                this!!.activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this!!.activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient!!.getLastLocation().addOnSuccessListener { location ->
            if (location != null) {
                if (locationManager != null) {
                    locationManager!!.getLastKnownLocation(location)
                }
            }
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient!!.removeLocationUpdates(locationCallback)
    }


}


