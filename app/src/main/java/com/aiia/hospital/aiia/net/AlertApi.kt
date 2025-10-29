package com.aiia.salud.net

import retrofit2.http.Body
import retrofit2.http.POST

data class AlertReq(
    val patientId: String,
    val emotion: String,
    val room: String,
    val note: String? = null
)

interface AlertApi {
    @POST("/api/alerts")
    suspend fun send(@Body body: AlertReq)
}
