package com.example.memories.model

import androidx.room.Embedded
import androidx.room.Relation

data class MemoryWithPhotos(
    @Embedded
    val memory: Memory,

    @Relation(entity = Photo::class,
    parentColumn = "id",
    entityColumn = "memoryId")
    val photos: List<Photo>
)