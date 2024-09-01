package com.dargoz.data.repositories

import com.dargoz.data.mappers.toEntity
import com.dargoz.data.sources.remote.RemoteDataSource
import com.dargoz.data.sources.remote.responses.FeatureResponse
import com.dargoz.domain.entities.FeatureEntity
import com.dargoz.domain.repositories.FeatureRepository
import kotlinx.io.IOException
import org.koin.core.annotation.Single

@Single
class FeatureRepositoryImpl(private val remoteDataSource: RemoteDataSource): FeatureRepository {

    override suspend fun getFeatureName(): FeatureEntity {
        val response = remoteDataSource.get("features", responseType = FeatureResponse::class)
        return when(response.httpCode) {
            200 -> response.body!!.toEntity()
            else -> throw IOException()
        }
    }
}