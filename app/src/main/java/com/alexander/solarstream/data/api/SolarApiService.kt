package com.alexander.solarstream.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data models mapping exactly to your Node.js req.body and res.json
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val message: String, val userPrefix: String, val status: String)

interface SolarApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

// Singleton to generate the Retrofit client
object RetrofitClient {
    // 10.0.2.2 is the special IP Android Emulators use to connect to your computer's localhost
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val apiService: SolarApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SolarApiService::class.java)
    }
}