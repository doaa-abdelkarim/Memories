package com.example.memories.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.memories.R
import com.example.memories.model.Memory
import com.example.memories.model.Photo

@Database(entities = [Memory::class, Photo::class], version = 1)
abstract class MemoriesDB : RoomDatabase() {

    abstract fun memoryDao(): MemoryDao

    companion object {
        private var instance: MemoriesDB? = null

        @Synchronized
        fun getInstance (context: Context): MemoriesDB {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoriesDB::class.java,
                    context.getString(R.string.db_name)
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return instance as MemoriesDB
        }
    }
}