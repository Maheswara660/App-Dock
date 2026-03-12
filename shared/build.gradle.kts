plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvmToolchain(17)
    androidTarget()

    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.multiplatform.settings)
                implementation(libs.kamel.image)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.sqldelight.coroutines.extensions)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.core.ktx)
                
                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.sqldelight.android.driver)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.sqldelight.sqlite.driver)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ktor.client.cio)
                implementation(libs.slf4j.simple)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.foss.appdock.shared"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

sqldelight {
    databases {
        create("AppDockDatabase") {
            packageName.set("com.foss.appdock.shared.database")
        }
    }
}
