plugins {
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.composeMultiplatform).apply(false)
}

// ── Dependency version alignment ──────────────────────────────────────────────
// Two separate libraries create conflicting transitive dependency upgrades:
//
// 1. Skiko: compose-webview-multiplatform 1.9.40 pins the macOS native runtime
//    to 0.8.4, while Compose 1.6.11 upgrades the JVM artifacts to 0.8.18.
//    The native/JVM version mismatch causes an UnsatisfiedLinkError at startup.
//
// 2. Ktor: compose-webview-multiplatform 1.9.40 upgrades ktor-client-core from
//    2.3.8 → 3.0.0 (a major, breaking version), while ktor-client-cio and all
//    other Ktor modules remain at 2.3.8. The mixed-version classpath causes a
//    ClassNotFoundException for internal Ktor 2.x plugin classes at runtime.
//
// Solution: Force all affected artifacts to the versions our own code targets.
subprojects {
    configurations.configureEach {
        resolutionStrategy {
            // Skiko: force all native platforms to match the JVM classes
            force("org.jetbrains.skiko:skiko:0.8.18")
            force("org.jetbrains.skiko:skiko-awt:0.8.18")
            force("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.8.18")
            force("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.8.18")
            force("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.8.18")
            force("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.8.18")

            // Ktor: keep the entire Ktor graph at 2.3.8 — prevent major-version
            // upgrades forced by compose-webview-multiplatform 1.9.40.
            force("io.ktor:ktor-client-core:2.3.8")
            force("io.ktor:ktor-client-core-jvm:2.3.8")
            force("io.ktor:ktor-client-cio:2.3.8")
            force("io.ktor:ktor-client-cio-jvm:2.3.8")
            force("io.ktor:ktor-client-content-negotiation:2.3.8")
            force("io.ktor:ktor-client-content-negotiation-jvm:2.3.8")
            force("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
            force("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.8")
            force("io.ktor:ktor-http:2.3.8")
            force("io.ktor:ktor-http-jvm:2.3.8")
            force("io.ktor:ktor-utils:2.3.8")
            force("io.ktor:ktor-utils-jvm:2.3.8")
            force("io.ktor:ktor-io:2.3.8")
            force("io.ktor:ktor-io-jvm:2.3.8")

            // Force reliable JOGL versions for Compose Desktop rendering
            force("org.jogamp.jogl:jogl-all:2.5.0")
            force("org.jogamp.gluegen:gluegen-rt:2.5.0")
        }
    }
}
