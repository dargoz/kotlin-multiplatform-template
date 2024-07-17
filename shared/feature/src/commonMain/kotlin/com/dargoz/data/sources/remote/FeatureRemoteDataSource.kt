package com.dargoz.data.sources.remote

import com.dargoz.data.sources.remote.responses.FeatureResponse

interface FeatureRemoteDataSource {

    suspend fun getFeature(): FeatureResponse

}