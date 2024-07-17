package com.dargoz.data.sources.remote.service

import com.dargoz.data.sources.remote.responses.FeatureResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.io.IOException

class FeatureServiceImpl(private val client: HttpClient): FeatureService {


    override suspend fun getFeatureService(): FeatureResponse {
        val response: FeatureResponse = client.get("features") {

        }.run {
            println("status : $status")
            println("body : ${body<FeatureResponse>()}")
            when(status) {
                HttpStatusCode.OK -> body()
                else -> throw IOException()
            }
        }

        return response
    }
}