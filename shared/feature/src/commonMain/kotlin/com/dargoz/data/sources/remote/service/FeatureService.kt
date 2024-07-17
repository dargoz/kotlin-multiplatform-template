package com.dargoz.data.sources.remote.service

import com.dargoz.data.sources.remote.responses.FeatureResponse

interface FeatureService {

    suspend fun getFeatureService(): FeatureResponse

}