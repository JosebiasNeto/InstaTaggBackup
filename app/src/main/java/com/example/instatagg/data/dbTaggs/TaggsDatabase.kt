package com.example.instatagg.data.dbTaggs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.instatagg.domain.model.Tagg

@Database(entities = [Tagg::class], version = 1)
abstract class TaggsDatabase : RoomDatabase() {

    abstract fun taggsDao(): TaggsDao

    companion object {
        @Volatile
        private var INSTANCE: TaggsDatabase? = null

        fun getDatabase(context: Context): TaggsDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaggsDatabase::class.java,
                    "photos_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}