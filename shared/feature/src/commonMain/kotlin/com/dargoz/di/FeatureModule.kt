package com.dargoz.di


import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module


import org.koin.ksp.generated.module

fun featureModule() = FeatureModule().module

@Module(includes = [CoreModule::class])
@ComponentScan("com.dargoz")
class FeatureModule {


}