# Overview

example markdown with mermaid:

```mermaid
flowchart TD
    A[Christmas] -->|Get money| B(Go shopping)
    B --> C{Let me think}
    C -->|One| D[Laptop]
    C -->|Two| E[iPhone]
    C -->|Three| F[fa:fa-car Car]
```

with snippet code:
```kotlin
package com.dargoz

import com.dargoz.di.featureModule
import com.dargoz.domain.entities.FeatureEntity
import com.dargoz.domain.usecases.FeatureUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

fun initFeatureKoin() {
    startKoin {
        modules(featureModule())
    }
}

class FeatureHelper: KoinComponent {
    private val featureUseCase: FeatureUseCase by inject()

    suspend fun executeFeatureUseCase(): IResult<FeatureEntity> {
        val useCase = featureUseCase("")
        return useCase
    }

}
```

another chart:
```mermaid
pie title Pets adopted by volunteers
    "Dogs" : 386
    "Cats" : 85
    "Rats" : 15
```