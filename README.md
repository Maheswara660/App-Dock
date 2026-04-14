<div align="center">
  <img src="shared/src/commonMain/resources/icon.png" width="128" height="128" alt="App Dock Logo" />
  <h1>App Dock</h1>
  <p><strong>A Privacy-First, Local-Only Workspace for your Web Applications</strong></p>

  <p>
    <img src="https://img.shields.io/badge/Kotlin-1.9.22-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Compose-1.6.0-000000?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
    <img src="https://img.shields.io/badge/JDK-21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="JDK" />
    <img src="https://img.shields.io/badge/Gradle-8.13-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white" alt="Gradle" />
  </p>

  ---

  <p>
    <img src="https://img.shields.io/badge/Windows-10+-0078D6?style=for-the-badge&logo=windows&logoColor=white" alt="Windows" />
    <img src="https://img.shields.io/badge/macOS-Sonoma+-000000?style=for-the-badge&logo=apple&logoColor=white" alt="macOS" />
    <img src="https://img.shields.io/badge/Linux-Stable-FCC624?style=for-the-badge&logo=linux&logoColor=black" alt="Linux" />
    <img src="https://img.shields.io/badge/Android-7.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  </p>

  ---
</div>

> [!CAUTION]
> **Project Status: Sunset Notice**
>
> As of April 15, 2026, **App-Dock** is officially entering a sunset phase and will no longer be actively maintained. Version **1.2.0** represents the final planned update for this project.
>
> Maintaining a complex, multi-platform ecosystem requires significant resources and dedicated time. Due to personal constraints and the heavy workload involved in managing cross-platform dependencies, I have made the difficult decision to discontinue active development.
>
> I want to thank everyone who supported this project. The code will remain available as an open-source archive (GPLv3) for anyone who wishes to fork it or learn from its implementation.

> [!IMPORTANT]
> **Privacy Manifesto**: App Dock is built on a strict privacy-first, local-only philosophy. We believe your workspace and data belong entirely to you. No telemetry, no accounts, no cloud sync—just your apps, your way.

## 🤔 Why App Dock?

Do you have 50 browser tabs open just for WhatsApp, Gmail, and Notion? **App Dock** fixes tab-overload. It turns any website into a standalone application that sits in your taskbar (Windows/Linux) or Dock (macOS).

- 🛡️ **Total Privacy**: Everything stays on your machine. No tracking, no data collection.
- 🚀 **Native Flow**: Desktop apps leverage your system browser explicitly, utilizing Chromium/Firefox flags seamlessly.
- 📂 **Dynamic Browser Discovery**: Automatically detects Arc, Vivaldi, Opera, Edge, Brave, and other Chromium-based or Firefox-based engines on your system.
- 👤 **Isolated Profiles**: Stay logged into two different accounts for the same site using unique profile directory partitioning.
- 🕵️ **Enhanced Incognito**: Forces RAM-only caching and wipes all history/cookies instantly on exit.

---

## ⚡ Quick Start

1. **Open App Dock** and click the **"+"** button.
2. **Type the URL** (like `web.whatsapp.com`) and give it a name.
3. **Select your Browser**: Choose from any browser installed on your computer.
4. **Save it!** Launch it directly from your dashboard or your computer's desktop.

---

## 📦 Installation Instructions

### 🍎 For Mac (macOS)
- **Standard (Recommended)**: Download the **.dmg** or **.pkg** installer from [Latest Releases](https://github.com/Maheswararr/App-Dock/releases). Drag the app to your Applications folder.
- **System Stability**: Our macOS bundles are built with modern PWA architecture to ensure smooth integration with the Dock and Mission Control.

---

### 🪟 For Windows
- **Standard (Recommended)**: Download the **.exe** or **.msi** installer from [Latest Releases](https://github.com/Maheswararr/App-Dock/releases).
- **Troubleshooting**: If Windows SmartScreen blocks it, click **"More Info"** and then **"Run Anyway"**.

---

### 🐧 For Linux
- **Debian/Ubuntu/Kali**: Download the **.deb** package.
- **Fedora/RHEL/CentOS**: Download the **.rpm** package.
- **Arch Linux**: Build using the provided `PKGBUILD`.
- **Installation**: `sudo dpkg -i App-Dock-*.deb` or `sudo rpm -i App-Dock-*.rpm`

---

### 🤖 For Android
- **Standard (Recommended)**: Download the **.apk** and open it on your device.
- **Troubleshooting**: Ensure **"Install from Unknown Sources"** is enabled in Settings.

---

## 🛠️ Common Fixes
- **App won't launch (Desktop)**: Ensure you have **Java 21** or later installed if building from source.
- **Building from source?**: Ensure you have the latest Gradle (8.13) and JDK 21.
- **Something else?**: Open an [Issue](https://github.com/Maheswararr/App-Dock/issues) and I'll help you out!

---

## 📜 Credits

- **Linux Mint Team**: For the [Web App Manager](https://github.com/linuxmint/webapp-manager) inspiration.
- **Peppermint OS Team**: The original creators of **ICE**.

---

## ❤️ Love the project?

App Dock is free and open-source. If it makes your digital life easier, feel free to support my work!

[<img src="https://storage.ko-fi.com/cdn/kofi3.png?v=3" height="36" alt="buy me a coffee at ko-fi.com" />](https://ko-fi.com/Maheswara660)

---

<div align="center">
  <p>Released under the <strong>GPLv3 License</strong></p>
  <i>Developed with ❤️ by <a href="https://github.com/Maheswara660">Maheswara660</a></i>
</div>
