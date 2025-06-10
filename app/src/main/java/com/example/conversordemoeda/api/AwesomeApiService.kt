package com.example.conversordemoeda.api

import com.example.conversordemoeda.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface AwesomeApiService {
    @GET("last/{pair}")
    suspend fun getRate(
        @Path("pair") pair: String,
        @Header("x-api-key") xApiKey: String
    ): Map<String , ExchangeRateResponse>
}