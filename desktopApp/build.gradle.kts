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
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            // macOS: TargetFormat.Dmg, TargetFormat.Pkg
            // Linux: TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage
            // Windows: TargetFormat.Exe, TargetFormat.Msi
            // Note: .pacman is not natively supported by Compose Multiplatform (jpackage).
            // A common workaround for Arch Linux is to build a .deb or AppImage and use AUR / PKGBUILD.
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Rpm,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.AppImage
            )
            packageName = "App Dock"
            packageVersion = "1.0.0"
        }
    }
}
