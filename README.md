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

> [!IMPORTANT]
> **Privacy Manifesto**: App Dock is built on a strict privacy-first, local-only philosophy. We believe your workspace and data belong entirely to you. No telemetry, no accounts, no cloud sync—just your apps, your way.

## 🤔 Why App Dock?

Do you have 50 browser tabs open just for WhatsApp, Gmail, and Notion? **App Dock** fixes tab-overload. It turns any website into a standalone application that sits in your taskbar (Windows/Linux) or Dock (macOS).

- 🛡️ **Total Privacy**: Everything stays on your machine. No tracking, no data collection.
- 🚀 **Embedded Desktop Experience**: Desktop standalone apps now run via a native **Chromium engine (KCEF)**, removing external browser clutter.
- 📱 **Immersive Android**: A custom, edge-to-edge native WebView for Android—no URL bars, just the app.
- 👤 **Isolated Profiles**: Stay logged into two different accounts for the same site using unique profile directory partitioning.
- 🕵️ **Enhanced Incognito**: Forces RAM-only caching and wipes all history/cookies instantly on exit.

---

## ⚡ Quick Start

1. **Open App Dock** and click the **"+"** button.
2. **Type the URL** (like `web.whatsapp.com`) and give it a name.
3. **Save it!** Launch it directly from your dashboard or your computer's desktop.

---

## 📦 Installation Instructions

### 🍎 For Mac (macOS)
- **Standard (Recommended)**: Download the `.dmg` from [Latest Releases](https://github.com/Maheswararr/App-Dock/releases). Drag and drop it to your Applications folder.
- **Advanced**: Use the `.pkg` for system-wide installs.
- **Troubleshooting**: If you see *"App cannot be opened because it is from an unidentified developer"*, go to **System Settings > Privacy & Security** and click **"Open Anyway"**.

---

### 🪟 For Windows
- **Standard (Recommended)**: Download the `.exe` installer from [Latest Releases](https://github.com/Maheswararr/App-Dock/releases).
- **Advanced**: Use the `.msi` for automated deployments.
- **Troubleshooting**: If Windows SmartScreen blocks it, click **"More Info"** and then **"Run Anyway"**.

---

### 🐧 For Linux
- **Recommended (AppImage)**: This is a "portable" app that works on almost any Linux version without installation.
  1. Download the `.AppImage` from [Latest Releases](https://github.com/Maheswararr/App-Dock/releases).
  2. Right-click the file and select **Properties > Permissions** and check **"Allow executing file as program"**.
  3. (Or via Terminal: `chmod +x App-Dock-*.AppImage`)
  4. Double-click to run!

#### **Step-by-Step Arch Linux Installation**
1. **Install Dependencies**: `sudo pacman -S --needed base-devel git jdk21-openjdk`
2. **Clone the Project**: `git clone https://github.com/Maheswararr/App-Dock.git && cd App-Dock`
3. **Build & Install**: `makepkg -si`

#### **Other Linux Distributions**
- **Debian/Ubuntu/Kali**: `sudo dpkg -i App-Dock-*.deb` (Fix with `sudo apt install -f`).
- **Fedora/RHEL/CentOS**: `sudo rpm -i App-Dock-*.rpm`

---

### 🤖 For Android
- **Standard (Recommended)**: Download the `.apk` and open it on your device.
- **Troubleshooting**: Ensure **"Install from Unknown Sources"** is enabled in Settings.

---

## 🛠️ Common Fixes
- **App won't launch (Desktop)**: Ensure you have **Java 21** or later installed if building from source.
- **Building from source?**: Ensure you have the latest Gradle (8.13) and JDK 21.
- **Something else?**: Open an [Issue](https://github.com/Maheswararr/App-Dock/issues) and we'll help you out!

---

## 📜 Credits

- **Linux Mint Team**: For the [Web App Manager](https://github.com/linuxmint/webapp-manager) inspiration.
- **Peppermint OS Team**: The original creators of **ICE**.
- **KCEF Project**: For powering the native desktop Chromium experience.

---

## ❤️ Love the project?

App Dock is free and open-source. If it makes your digital life easier, feel free to support our work!

[<img src="https://storage.ko-fi.com/cdn/kofi3.png?v=3" height="36" alt="buy me a coffee at ko-fi.com" />](https://ko-fi.com/Maheswara660)

---

<div align="center">
  <p>Released under the <strong>GPLv3 License</strong></p>
  <i>Developed with ❤️ by <a href="https://github.com/Maheswara660">Maheswara660</a></i>
</div>
