pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
    }
}

rootProject.name = "vid-con"

plugins {
    kotlin("multiplatform") version "1.8.10" apply false
    kotlin("plugin.serialization") version "1.8.10" apply false
    id("org.jetbrains.compose") version "1.3.1" apply false
    id("io.ktor.plugin") version "2.2.4" apply false
}
