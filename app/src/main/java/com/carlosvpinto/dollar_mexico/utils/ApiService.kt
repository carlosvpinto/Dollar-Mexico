package com.carlosvpinto.dollar_mexico.utils


import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
//    @GET
//    suspend fun getBancos(@Url url: String): BancosModel


    @GET("dollar")
    suspend fun getDollar(): ApiMexicoResponse




}