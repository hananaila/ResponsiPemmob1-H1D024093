package com.pemmob.bmkg.data.network

import com.pemmob.bmkg.data.model.GempaResponse
import retrofit2.http.GET

interface ApiInterface {
    @GET("DataMKG/TEWS/gempaterkini.json")
    suspend fun getGempaTerkini(): GempaResponse
}
