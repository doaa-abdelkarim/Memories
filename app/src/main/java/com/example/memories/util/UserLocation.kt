package com.example.memories.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class UserLocation {

    @SuppressLint("MissingPermission")
    fun getUserLocation(
        context: Context,
        onSuccessListener: OnSuccessListener<Location>
    ) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener(onSuccessListener)
    }
}