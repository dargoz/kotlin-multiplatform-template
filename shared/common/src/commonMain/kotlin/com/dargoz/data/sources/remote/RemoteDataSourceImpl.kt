package com.dargoz.data.sources.remote

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.koin.core.annotation.Single
import kotlin.reflect.KClass



@Single
class RemoteDataSourceImpl(private val client: HttpClient): RemoteDataSource {

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

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T: Any> post(url: String, request: Request, responseType: KClass<T>): Response<T> {
        val response = client.post(url) {
            contentType(request.toContentType())
            headers {
                request.headers.map {
                    append(it.key, it.value)
                }
            }
            when(request) {
                is Request.ApplicationJson -> setBody(request.json)
                is Request.MultiPart -> setBody(
                    MultiPartFormDataContent(
                    formData {
                        append("description", "Ktor logo")
                        append("image", request.file.data, Headers.build {
                            append(HttpHeaders.ContentType, request.file.contentType)
                            append(HttpHeaders.ContentDisposition, "filename=\"${request.file.fileName}\"")
                        })
                    },
                    boundary = "WebAppBoundary"
                )
                )
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

    override suspend fun <T: Any> put(url: String, request: Request,  responseType: KClass<T>): Response<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> delete(url: String, request: Request): Response<T> {
        TODO("Not yet implemented")
    }

    private fun Request.toContentType(): ContentType {
        return when(this) {
            is Request.ApplicationJson -> ContentType.Application.Json
            is Request.MultiPart -> ContentType.MultiPart.FormData
        }
    }

}