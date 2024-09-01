package com.dargoz.di

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

import org.koin.ksp.generated.module

fun coreModule() = CoreModule().module

@Module
@ComponentScan("com.dargoz")
class CoreModule {

    @Single
    fun httpClient() = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                allowStructuredMapKeys = true
                prettyPrint = false
                useArrayPolymorphism = false
                // namingStrategy = JsonNamingStrategy.SnakeCase
            })
        }
        defaultRequest {
            url("http://localhost:8080/")
        }
    }
}