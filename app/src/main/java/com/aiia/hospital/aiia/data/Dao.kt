package com.aiia.hospital.aiia.data

import androidx.room.*

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: Patient)
    @Query("SELECT * FROM Patient WHERE id = :id") suspend fun byId(id: String): Patient?
}

@Dao
interface ObservationDao {
    @Insert suspend fun add(o: Observation)
    @Query("SELECT * FROM Observation WHERE patientId = :pid ORDER BY timestamp DESC")
    suspend fun byPatient(pid: String): List<Observation>
}
