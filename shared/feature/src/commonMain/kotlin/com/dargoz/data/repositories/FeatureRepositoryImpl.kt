package com.dargoz.data.repositories

import com.dargoz.data.mappers.toEntity
import com.dargoz.data.sources.remote.FeatureRemoteDataSource
import com.dargoz.domain.entities.FeatureEntity
import com.dargoz.domain.repositories.FeatureRepository

class FeatureRepositoryImpl(private val featureRemoteDataSource: FeatureRemoteDataSource): FeatureRepository {

    override suspend fun getFeatureName(): FeatureEntity {
        return featureRemoteDataSource.getFeature().toEntity()
    }
}