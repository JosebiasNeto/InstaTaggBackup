package com.jnsoft.instatagg.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jnsoft.instatagg.domain.model.PhotoEntity
import com.jnsoft.instatagg.domain.model.Tagg

@Database(version = 3, entities = [PhotoEntity::class, Tagg::class],
          autoMigrations = [
              AutoMigration (from = 1, to = 2),
              AutoMigration (from = 2, to = 3)
          ])
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
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}