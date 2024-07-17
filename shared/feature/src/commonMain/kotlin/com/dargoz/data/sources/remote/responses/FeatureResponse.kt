package com.dargoz.data.sources.remote.responses

import kotlinx.serialization.Serializable

@Serializable
data class FeatureResponse(
    val id: Int,
    val name: String
)
