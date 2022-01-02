package com.jnsoft.instatagg.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jnsoft.instatagg.domain.model.PhotoEntity
import com.jnsoft.instatagg.domain.model.Tagg

@Database(version = 1, entities = [PhotoEntity::class, Tagg::class])
abstract class MainDatabase : RoomDatabase() {

    abstract fun photosDao(): PhotosDao
    abstract fun taggsDao(): TaggsDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "main_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}