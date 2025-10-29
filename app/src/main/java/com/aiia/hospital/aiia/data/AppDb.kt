package com.aiia.salud.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Patient::class, Observation::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun patient(): PatientDao
    abstract fun observation(): ObservationDao

    companion object {
        @Volatile private var I: AppDb? = null
        fun get(ctx: Context): AppDb = I ?: synchronized(this) {
            I ?: Room.databaseBuilder(ctx, AppDb::class.java, "aiia_salud.db").build().also { I = it }
        }
    }
}
