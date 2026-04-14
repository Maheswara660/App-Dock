pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://www.jogamp.org/deployment/maven/")
        maven("https://www.jcenter.bintray.com") // Fallback for legacy artifacts
    }

    plugins {
        id("com.android.application")
        id("com.android.library")
        id("org.jetbrains.compose")
        id("org.jetbrains.kotlin.multiplatform")
        id("app.cash.sqldelight")
        id("org.jetbrains.kotlin.plugin.serialization")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "AppDock"

include(":androidApp")
include(":shared")
include(":desktopApp")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://www.jogamp.org/deployment/maven/")
        maven("https://www.jcenter.bintray.com") // Fallback for legacy artifacts
    }
}


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
