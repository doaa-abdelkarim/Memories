package com.example.memories.db

import androidx.room.*
import com.example.memories.model.Memory
import com.example.memories.model.MemoryWithPhotos
import com.example.memories.model.Photo

@Dao
interface MemoryDao {
    @Insert
    fun insertMemory(memory: Memory): Long

    @Update
    fun updateMemory(memory: Memory)

    @Delete
    fun deleteMemory(memory: Memory)

    @Query("DELETE FROM memories")
    fun deleteAllMemories()

    @Query("SELECT * FROM memories")
    fun getAllMemories(): List<Memory>

    @Insert
    fun addPhotos(photos: List<Photo>)

    @Delete
    fun deletePhoto(photo: Photo)

    @Query("DELETE FROM photos WHERE memoryId = :memoryId")
    fun deletePhotoByMemoryId (memoryId: Long)

    @Query("SELECT * From memories WHERE id = :id")
    fun getMemoryWithPhotosById(id: Long): MemoryWithPhotos

    @Transaction
    @Query("SELECT * From memories ORDER BY id DESC")
    fun getAllMemoriesWithPhotos(): List<MemoryWithPhotos>

    @Query("SELECT * From memories WHERE latitude >= :minLat AND latitude <= :maxLat AND longitude >= :minLong AND longitude <= :maxLong")
    fun getMemoriesByLocation(minLat: Double, maxLat: Double, minLong:Double, maxLong: Double): List<Memory>

    @Query("UPDATE memories SET lastTimeNotified = :currentTime WHERE id = :id" )
    fun updateMemoryLastTimeNotified(id: Long?, currentTime: Long)

    @Query("SELECT lastTimeNotified From memories WHERE id = :id")
    fun getLastTimeNotifiedById(id: Long): Long

}