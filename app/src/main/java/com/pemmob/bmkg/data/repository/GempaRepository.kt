package com.pemmob.bmkg.data.repository

import com.pemmob.bmkg.data.model.Gempa
import com.pemmob.bmkg.data.network.ApiClient

class GempaRepository {
    suspend fun getGempaList(): List<Gempa> {
        return ApiClient.instance.getGempaTerkini().infoGempa.gempa
    }
}
