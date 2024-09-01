package com.dargoz.data.sources.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.koin.core.annotation.Single
import kotlin.reflect.KClass



@Single
class RemoteDataSourceImpl constructor(private val client: HttpClient): RemoteDataSource {

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T: Any> get(url: String, headers: Map<String, String>, responseType: KClass<T>): Response<T> {
        val response = client.get(url) {
            headers {
                headers.map {
                    append(it.key, it.value)
                }
            }
        }
        val body = response.bodyAsText()
        val parsedResponse = Json.decodeFromString(responseType.serializer(), body)
        return Response(
            httpCode = response.status.value,
            headers = response.headers.toMap(),
            body = parsedResponse
        )
    }

    override suspend fun <T> post(url: String, headers: Map<String, String>): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> put(url: String, headers: Map<String, String>): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> delete(url: String, headers: Map<String, String>): Response<T> {
        TODO("Not yet implemented")
    }

}