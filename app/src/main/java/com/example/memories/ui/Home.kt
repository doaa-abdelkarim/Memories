package com.example.memories.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.memories.service.LocationService

class Home(val context: Context) {

    fun startLocationService () {
        ContextCompat.startForegroundService(
            context,
            Intent(context, LocationService::class.java)
        )
    }
}