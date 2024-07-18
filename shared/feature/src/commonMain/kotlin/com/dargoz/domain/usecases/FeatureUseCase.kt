package com.dargoz.domain.usecases

import com.dargoz.UseCase
import com.dargoz.domain.entities.FeatureEntity
import com.dargoz.domain.repositories.FeatureRepository
import org.koin.core.annotation.Single

@Single
class FeatureUseCase(private val featureRepository: FeatureRepository): UseCase<FeatureEntity, String>() {

    override suspend fun execute(param: String): FeatureEntity {
        return featureRepository.getFeatureName()
    }

}