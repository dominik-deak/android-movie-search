package com.example.coursework2

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room database class
 */
@Database(entities = [Movie::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
