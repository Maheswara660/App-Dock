import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    jvmToolchain(17)
    jvm("desktop")
    sourceSets {
        val desktopMain by getting  {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":shared"))
                implementation(libs.compose.webview.multiplatform)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            // macOS: TargetFormat.Dmg, TargetFormat.Pkg
            // Linux: TargetFormat.Deb, TargetFormat.Rpm
            // Windows: TargetFormat.Exe, TargetFormat.Msi
            // Note: .pacman is not natively supported by Compose Multiplatform (jpackage).
            // A common workaround for Arch Linux is to build a .deb and use AUR / PKGBUILD.
            // jpackage only supports native formats for the host OS.
            // We define them based on the operating system running the build.
            val os = System.getProperty("os.name").toLowerCase()
            val availableFormats = when {
                os.contains("win") -> listOf(TargetFormat.Exe)
                os.contains("mac") -> listOf(TargetFormat.Dmg)
                else -> listOf(TargetFormat.AppImage)
            }
            targetFormats(*availableFormats.toTypedArray())
            packageName = "AppDock"
            packageVersion = "1.1.0"
            vendor = "AppDock"
            description = "App Dock - Modern Web App Manager"
            copyright = "© 2026 AppDock"
            
            modules("java.sql")

            windows {
                shortcut = true
                menu = true
                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
                upgradeUuid = "a3b8d1b6-0b3b-4b1a-9c1a-1a2b3c4d5e6f" // Consistent UUID for upgrades
            }

            macOS {
                bundleID = "com.foss.appdock"
                iconFile.set(project.file("src/desktopMain/resources/myicon.icns"))
            }
        }
    }
}
