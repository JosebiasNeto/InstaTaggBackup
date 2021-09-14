package com.example.instatagg.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.instatagg.domain.model.PhotoEntity

@Database(entities = [PhotoEntity::class], version = 1)
abstract class PhotosDatabase : RoomDatabase() {

    abstract fun photosDao(): PhotosDao

    companion object {
        @Volatile
        private var INSTANCE: PhotosDatabase? = null

        fun getDatabase(context: Context): PhotosDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotosDatabase::class.java,
                    "photos_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}