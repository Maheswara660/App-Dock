# Changelog

All notable changes to the **App-Dock** project will be documented in this file. This project adheres to [Semantic Versioning](https://semver.org/).

## [1.2.0] - 2026-04-15

### Added
- **Dynamic Browser Discovery**: Automatically detects and lists all installed browsers including Arc, Vivaldi, Opera, Edge, Brave, and other Chromium/Firefox forks.
- **Modern macOS Architecture**: Unified shell-script launcher that applies native PWA metadata standards (LSEnvironment, MallocNanoZone) for high-quality Dock integration.
- **Native Singleton Behavior**: Shortcuts now correctly detect existing profiles and focus running windows instead of opening blank browser tabs.

### Fixed
- **macOS Compatibility**: Resolved "App not supported on this Mac" errors on Monterey, Ventura, and Sonoma.
- **Dock Integration**: Fixed issues where web apps would group incorrectly or fail to reactivate from the Dock.
- **Profile Initialization**: Improved the reliability of isolated profile creation for Chromium-based browsers.

---

## [1.1.0] - 2024-03-01
- Initial public release with basic web app management and custom browser support.
- Support for system themes and accent colors.
- Local backup and restore functionality.
