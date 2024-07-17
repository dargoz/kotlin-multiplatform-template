package com.dargoz.data.sources.remote

import com.dargoz.data.sources.remote.responses.FeatureResponse
import com.dargoz.data.sources.remote.service.FeatureService

class FeatureRemoteDataSourceImpl (private val featureService: FeatureService): FeatureRemoteDataSource {

    override suspend fun getFeature(): FeatureResponse {
        return featureService.getFeatureService()
    }
}