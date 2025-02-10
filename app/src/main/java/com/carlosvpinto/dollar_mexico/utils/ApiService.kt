package com.carlosvpinto.dollar_mexico.utils


import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponse
import com.carlosvpinto.dollar_mexico.model.history.HistoryModelResponse
import com.carlosvpinto.dollar_mexico.model.history.HistoryModelResponseItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("dollar")
    suspend fun getDollar(): ApiMexicoResponse

    @GET("historical_dollar/7d")
    suspend fun getDollarHistory7D(): List<HistoryModelResponseItem> // Devuelve una lista

    @GET("historical_dollar/30d")
    suspend fun getDollarHistory30D(): List<HistoryModelResponseItem> // Devuelve una lista
}
