package com.example.memories.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class TrackingLocation(val context: Context) {

    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationClient:FusedLocationProviderClient

    fun createLocationRequest() {
        //Create the location request and set the parameters
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun defineLocationUpdateCallback(onLocationUpdateListener: OnLocationUpdateListener) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    onLocationUpdateListener.onLocationUpdate(location)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    fun stopLocationUpdates() =
        fusedLocationClient.removeLocationUpdates(locationCallback)

    interface OnLocationUpdateListener {
        fun onLocationUpdate (location: Location)
    }
}