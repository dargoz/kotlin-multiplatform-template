plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "com.example"
version = "0.0.1"


dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.symbol.processing.api)
    implementation(libs.kotlin.compiler.embeddable)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}