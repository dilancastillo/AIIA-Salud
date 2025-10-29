package com.aiia.salud.net

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetModule {
    // TODO: pon aquí la URL de tu tablero (http o https)
    private const val BASE_URL = "http://TU_SERVIDOR:PUERTO/"

    val api: AlertApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlertApi::class.java)
    }
}
