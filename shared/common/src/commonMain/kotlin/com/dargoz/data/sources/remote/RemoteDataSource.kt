package com.dargoz.data.sources.remote

import kotlin.reflect.KClass

interface RemoteDataSource {

    suspend fun<T: Any> get(url: String, headers: Map<String, String> = emptyMap(), responseType: KClass<T>): Response<T>

    suspend fun<T> post(url: String, headers: Map<String, String>): Response<T>

    suspend fun<T> put(url: String, headers: Map<String, String>): Response<T>

    suspend fun<T> delete(url: String, headers: Map<String, String>): Response<T>

}