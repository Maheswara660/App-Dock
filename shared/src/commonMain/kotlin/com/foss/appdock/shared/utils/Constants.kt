package com.foss.appdock.shared.utils

/**
 * Global Constants for App-Dock v2.0.0 (Proxima).
 */
object Constants {
    const val APP_NAME = "App-Dock"
    const val VERSION = "1.2.0"
    const val DEVELOPER = "Maheswara660"
    
    /**
     * Project Acknowledgement as requested by the developer.
     */
    const val ACKNOWLEDGEMENT = "I have used logic and code foundations from external open-source projects to architect and build this version of the project."
    
    val CHANGELOG = listOf(
        "Implemented dynamic browser discovery (Arc, Vivaldi, Opera, Edge, etc.).",
        "Standardized macOS .app bundle architecture for Monterey/Sonoma.",
        "Fixed Singleton window behavior and Dock re-activation issues.",
        "Upgraded build system to JDK 21 for enhanced performance."
    )

    fun getCopyright(): String {
        val year = getCurrentYear()
        return "© $year $DEVELOPER. All rights reserved."
    }
}
