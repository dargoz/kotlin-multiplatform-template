package com.dargoz.domain.repositories

import com.dargoz.domain.entities.FeatureEntity

interface FeatureRepository {

    suspend fun getFeatureName(): FeatureEntity

    suspend fun getFeatureList(): List<FeatureEntity>
}