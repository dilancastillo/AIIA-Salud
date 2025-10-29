package com.aiia.salud.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Patient(
    @PrimaryKey val id: String,        // documento / historia
    val name: String,
    val room: String                   // ubicación clínica
)

@Entity
data class Observation(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val patientId: String,
    val timestamp: Long,
    val emotion: String,               // happy | sad | neutral
    val note: String?
)
