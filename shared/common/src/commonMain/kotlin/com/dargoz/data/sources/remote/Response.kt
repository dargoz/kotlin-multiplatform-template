package com.dargoz.data.sources.remote

data class Response<T>(
    val httpCode: Int = 200,
    val headers: Map<String, List<String>> = emptyMap(),
    val body: T? = null
)
