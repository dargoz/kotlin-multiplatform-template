package com.dargoz.data.mappers

import com.dargoz.data.sources.remote.responses.FeatureResponse
import com.dargoz.domain.entities.FeatureEntity


fun FeatureResponse.toEntity(): FeatureEntity {
    return FeatureEntity(name = this.name)
}