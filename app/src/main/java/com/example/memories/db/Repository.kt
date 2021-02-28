package com.example.memories.db

import android.content.Context
import android.os.AsyncTask
import com.example.memories.model.Memory
import com.example.memories.model.MemoryWithPhotos
import com.example.memories.model.Photo

class Repository (context: Context) {

    val memoryDao = MemoriesDB.getInstance(context).memoryDao()

    fun deleteMemory(memory: Memory) = DeleteMemoryAsyncTask(memoryDao).execute(memory)

    fun deleteAllMemories() = DeleteAllMemoriesAsyncTask(memoryDao).execute()

    fun insertMemoryWithPhotos(memoryWithPhotos: MemoryWithPhotos) =
        InsertMemoryWithPhotosAsyncTask(memoryDao).execute(memoryWithPhotos)

    fun updateMemoryWithPhotos(memoryWithPhotos: MemoryWithPhotos) =
        UpdateMemoryWithPhotosAsyncTask(memoryDao).execute(memoryWithPhotos)

    fun getMemoryWithPhotosById(memoryId: Long) =
        GetMemoryWithPhotosByIdAsyncTask(memoryDao).execute(memoryId).get()

    fun getAllMemoriesWithPhotos() = GetAllMemoriesWithPhotosAsyncTask(memoryDao).execute().get()

    fun getMemoriesByLocation(minLat: Double, maxLat: Double, minLong: Double, maxLong: Double) =
        GetMemoriesByLocationAsyncTask(memoryDao).execute(minLat, maxLat, minLong, maxLong).get()

    fun updateLastTimeNotified(id: Long?) =
        UpdateLastTimeNotifiedAsyncTask(memoryDao).execute(id)

    fun getLastTimeNotifiedById(id: Long?) =
        GetLastTimeNotifiedByIdAsyncTask(memoryDao).execute(id).get()

    private class DeleteMemoryAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<Memory, Void, Void?>() {
        override fun doInBackground(vararg memories: Memory?): Void? {

            if (memories.isNotEmpty())
                memories[0]?.let { memoryDao.deleteMemory(it) }
            return null
        }
    }

    private class DeleteAllMemoriesAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<Void, Void, Void?>() {
        override fun doInBackground(vararg p0: Void?): Void? {

            memoryDao.deleteAllMemories()
            return null
        }
    }

    private class DeletePhotoAsyncTask(val memoryDao: MemoryDao) : AsyncTask<Photo, Void, Void?>() {
        override fun doInBackground(vararg photos: Photo?): Void? {
            if (photos.isNotEmpty())
                photos[0]?.let { memoryDao.deletePhoto(it) }
            return null
        }
    }

    private class InsertMemoryWithPhotosAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<MemoryWithPhotos, Void, Void?>() {
        override fun doInBackground(vararg memoriesWithStudents: MemoryWithPhotos?): Void? {
            if (memoriesWithStudents.isNotEmpty())
                memoriesWithStudents[0]?.let {
                    val identifier = memoryDao.insertMemory(it.memory)
                    for (photo in it.photos)
                        photo.memoryId = identifier
                    memoryDao.addPhotos(it.photos)
                }
            return null
        }
    }

    private class UpdateMemoryWithPhotosAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<MemoryWithPhotos, Void, Void?>() {
        override fun doInBackground(vararg memoriesWithStudents: MemoryWithPhotos?): Void? {
            if (memoriesWithStudents.isNotEmpty())
                memoriesWithStudents[0]?.let {
                    memoryDao.updateMemory(it.memory)
                    memoryDao.deletePhotoByMemoryId(it.memory.id!!)
                    for (photo in it.photos)
                        photo.memoryId = it.memory.id
                    memoryDao.addPhotos(it.photos)
                }
            return null
        }
    }

    private class GetMemoryWithPhotosByIdAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<Long, Void, MemoryWithPhotos?>() {
        override fun doInBackground(vararg ids: Long?): MemoryWithPhotos? =
            if (ids.isNotEmpty())
                memoryDao.getMemoryWithPhotosById(ids[0] ?: -1)
            else null
    }

    private class GetAllMemoriesWithPhotosAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<Void, Void, ArrayList<MemoryWithPhotos>?>() {
        override fun doInBackground(vararg p0: Void?): ArrayList<MemoryWithPhotos>? {
            return memoryDao.getAllMemoriesWithPhotos() as ArrayList<MemoryWithPhotos>
        }
    }

    private class GetMemoriesByLocationAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<Double, Void, ArrayList<Memory>?>() {
        override fun doInBackground(vararg coordinates: Double?): ArrayList<Memory>? {
            if (coordinates.isNotEmpty())
                return memoryDao.getMemoriesByLocation(
                    coordinates[0]!!,
                    coordinates[1]!!,
                    coordinates[2]!!,
                    coordinates[3]!!
                ) as ArrayList<Memory>
            return null
        }

    }

    private class UpdateLastTimeNotifiedAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<Long?, Void, Void?>() {
        override fun doInBackground(vararg ids: Long?): Void? {
            if (ids.isNotEmpty())
                memoryDao.updateMemoryLastTimeNotified(ids[0], System.currentTimeMillis())
            return null
        }
    }

    private class GetLastTimeNotifiedByIdAsyncTask(val memoryDao: MemoryDao) :
        AsyncTask<Long?, Void, Long>() {
        override fun doInBackground(vararg ids: Long?): Long {
            val id = ids[0]
            if (ids.isEmpty() || id == null)
                return -1
            else
                return memoryDao.getLastTimeNotifiedById(id)
        }
    }
}