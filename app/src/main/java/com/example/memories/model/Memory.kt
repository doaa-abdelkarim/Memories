package com.example.memories.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val description: String,
    val time: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val lastTimeNotified: Long = -1
)