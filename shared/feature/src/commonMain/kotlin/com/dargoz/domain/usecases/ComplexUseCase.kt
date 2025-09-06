package com.dargoz.domain.usecases

import com.dargoz.UseCase
import com.dargoz.domain.entities.FeatureEntity
import com.dargoz.domain.repositories.FeatureRepository

class ComplexUseCase(private val repo: FeatureRepository) : UseCase<List<FeatureEntity>, String>() {

    override suspend fun execute(param: String): List<FeatureEntity> {
        val results = mutableListOf<FeatureEntity>()

        if (param.isEmpty()) {
            println("No param provided")
            return emptyList()
        } else if (param == "core") {
            results.add(FeatureEntity("core"))
        } else {
            for (item in repo.getFeatureList()) {
                if (item.name.startsWith("X")) {
                    println("Skipping $item")
                    continue
                }
                results.add(FeatureEntity(item.name))
            }
        }

        var i = 0
        while (i < results.size) {
            val f = results[i]
            if (f.name.length > 5) {
                println("Trimming ${f.name}")
            }
            i++
        }

        println("Finished processing ${results.size} items")
        return results
    }
}