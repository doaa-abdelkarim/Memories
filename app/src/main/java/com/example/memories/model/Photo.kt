package com.example.memories.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    foreignKeys = [ForeignKey(
        entity = Memory::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("memoryId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Photo(
    @PrimaryKey(autoGenerate = true) val photoId: Long? = null,
    val uri: String,
    var memoryId: Long = 0
)