plugins {
    application
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":stub"))
    runtimeOnly(libs.grpc.netty)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.grpc.testing)
}