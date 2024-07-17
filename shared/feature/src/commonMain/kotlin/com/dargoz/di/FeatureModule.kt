package com.dargoz.di

import com.dargoz.data.repositories.FeatureRepositoryImpl
import com.dargoz.data.sources.remote.FeatureRemoteDataSource
import com.dargoz.data.sources.remote.FeatureRemoteDataSourceImpl
import com.dargoz.data.sources.remote.service.FeatureService
import com.dargoz.data.sources.remote.service.FeatureServiceImpl
import com.dargoz.domain.repositories.FeatureRepository
import com.dargoz.domain.usecases.FeatureUseCase
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy


import org.koin.dsl.module

fun featureModule() = module {
    single<HttpClient> {
        HttpClient {
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
    single<FeatureRepository> { FeatureRepositoryImpl(get()) }
    single<FeatureRemoteDataSource> { FeatureRemoteDataSourceImpl(get()) }
    single<FeatureService> { FeatureServiceImpl(get()) }
    single<FeatureUseCase> { FeatureUseCase(get()) }
}