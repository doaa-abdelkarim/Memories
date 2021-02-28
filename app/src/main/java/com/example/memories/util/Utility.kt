package com.example.memories.util

import java.text.SimpleDateFormat
import java.util.*

class Utility {
    companion object {
        fun getCurrentTime() = Calendar.getInstance().timeInMillis


        fun formatTime(date: Date): String =
            SimpleDateFormat("E, dd MMM YYYY hh:mm:ss a")
                .format(date)
    }
}