package com.dargoz

import com.dargoz.domain.usecases.FeatureUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FeatureHelper: KoinComponent {
    private val featureUseCase: FeatureUseCase by inject()


}