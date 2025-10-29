package com.aiia.hospital.aiia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos principal de la aplicación.
 * Contiene las entidades Patient y Observation.
 */
@Database(
    entities = [Patient::class, Observation::class],
    version = 1,
    exportSchema = false // ✅ Evita el warning de Room
)
abstract class AppDb : RoomDatabase() {

    abstract fun patient(): PatientDao
    abstract fun observation(): ObservationDao

    companion object {

        // Singleton: garantiza una sola instancia de la BD en toda la app
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "aiia_salud.db"
                )
                    // Si cambias estructuras de tablas y no tienes migraciones, usar fallback
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
