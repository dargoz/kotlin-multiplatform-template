package com.dargoz.data.sources.remote

import kotlin.reflect.KClass

interface RemoteDataSource {

    suspend fun<T: Any> get(url: String, headers: Map<String, String> = emptyMap(), responseType: KClass<T>): Response<T>

    suspend fun<T: Any> post(url: String, request: Request,  responseType: KClass<T>): Response<T>

    suspend fun<T: Any> put(url: String, request: Request,  responseType: KClass<T>): Response<T>

    suspend fun<T> delete(url: String, request: Request): Response<T>

}