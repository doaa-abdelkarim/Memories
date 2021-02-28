package com.example.memories.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.memories.R
import com.example.memories.db.Repository
import com.example.memories.model.Memory
import com.example.memories.ui.HomeView
import com.example.memories.util.Constants
import com.example.memories.util.TrackingLocation
import com.example.memories.util.Utility
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.PI
import kotlin.math.cos


class LocationService : Service() {

    lateinit var trackingLocation: TrackingLocation
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()

        val notificationIntent = Intent(this, HomeView::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent, 0
        )
        val notification =
            NotificationCompat.Builder(this, Constants.CHANNEL_1_ID)
                .setContentTitle(getString(R.string.service_notification_title))
                .setContentText(getString(R.string.service_notification_content))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTrackingLocation()
        Timer().schedule(
            timerTask {
                trackingLocation.startLocationUpdates()
            },
            0,
            60 * 60 * 1000
        )
        return START_NOT_STICKY
    }

    private fun initTrackingLocation() {
        trackingLocation = TrackingLocation(this)
        trackingLocation.createLocationRequest()
        trackingLocation.defineLocationUpdateCallback(object :
            TrackingLocation.OnLocationUpdateListener {
            override fun onLocationUpdate(location: Location) {
                location.apply {
                    val memories = searchForLocation(location)
                    if (memories != null && memories.isNotEmpty()) {
                        for ((id, memory) in memories.withIndex()) {
                            //To notify user of his memory after 24 hours of storing it when he visit the same place
                            val memoryTimePlus24Hours = Calendar.getInstance()
                            memoryTimePlus24Hours.timeInMillis = memory.time
                            memoryTimePlus24Hours.add(Calendar.DATE, 1)
                            if (isNotifiedRecently(memory.lastTimeNotified) || Calendar.getInstance() < memoryTimePlus24Hours)
                                continue
                            else {
                                updateLastTimeNotified(memory.id)
                                showNotification(
                                    id + 2,
                                    "${memory.description}\n${Utility.formatTime(Date(memory.time))}"
                                )
                            }
                        }
                    }
                    trackingLocation.stopLocationUpdates()
                }
            }
        }
        )
    }

    private fun searchForLocation(location: Location): ArrayList<Memory>? {
        val minLat = location.latitude + (-0.5 / 6378) * (180 / PI)
        val minLong = location.longitude + (-0.5 / 6378) * (180 / PI) /
                cos(location.latitude * PI / 180)

        val maxLat = location.latitude + (0.5 / 6378) * (180 / PI)
        val maxLong = location.longitude + (0.5 / 6378) * (180 / PI) /
                cos(location.latitude * PI / 180)

        val repository = Repository(this)

        return repository
            .getMemoriesByLocation(
                minLat, maxLat,
                minLong, maxLong
            )
    }

    private fun isNotifiedRecently(lastTimeNotified: Long): Boolean {
        return if (lastTimeNotified == -1L)
            false
        else {
            val nextTimeNotified = Calendar.getInstance()
            nextTimeNotified.timeInMillis = lastTimeNotified
            nextTimeNotified.add(Calendar.DATE, 1)
            nextTimeNotified.timeInMillis > System.currentTimeMillis()
        }
    }

    private fun updateLastTimeNotified(id: Long?) =
        Repository(this).updateLastTimeNotified(id)

    private fun showNotification(id: Int, content: String) {
        val notification = NotificationCompat
            .Builder(this, Constants.CHANNEL_2_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(Color.BLUE)
            .build()

        NotificationManagerCompat
            .from(this)
            .notify(id, notification)
    }
}
