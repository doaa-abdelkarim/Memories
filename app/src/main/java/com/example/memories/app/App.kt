package com.example.memories.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.memories.util.Constants.Companion.CHANNEL_1_ID
import com.example.memories.util.Constants.Companion.CHANNEL_2_ID


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val serviceChannel = NotificationChannel(
                    CHANNEL_1_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)

                val memoryChannel =  NotificationChannel(CHANNEL_2_ID,
                    "Memories Channel",
                    NotificationManager.IMPORTANCE_HIGH)

                val manager = getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(serviceChannel)
                manager.createNotificationChannel(memoryChannel)

        }
    }
}