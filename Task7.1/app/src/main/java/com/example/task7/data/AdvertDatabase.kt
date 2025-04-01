package com.example.task7.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.task7.model.Advert

@Database(entities = [Advert::class], version = 1, exportSchema = false)
abstract class AdvertDatabase : RoomDatabase() {
    abstract fun advertDao(): AdvertDao

    companion object {
        @Volatile
        private var INSTANCE: AdvertDatabase? = null

        fun getDatabase(context: Context): AdvertDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AdvertDatabase::class.java,
                    "advert_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}