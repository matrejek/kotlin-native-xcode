pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

enableFeaturePreview("GRADLE_METADATA")

include(":KotlinNativeFramework")
project(":KotlinNativeFramework").projectDir = file("../KotlinNativeFramework")
rootProject.name = file("..").name
