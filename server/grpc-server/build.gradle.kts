plugins {
    application
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(project(":stub"))
    runtimeOnly(libs.grpc.netty)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.grpc.testing)
}