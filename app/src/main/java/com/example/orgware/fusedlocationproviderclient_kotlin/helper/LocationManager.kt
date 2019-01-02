package com.example.orgware.fusedlocationproviderclient_kotlin.helper

import android.location.Location

interface LocationManager {
    fun onLocationChanged(location: Location?)

    fun getLastKnownLocation(location: Location?)
}