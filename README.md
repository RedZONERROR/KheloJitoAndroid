<div align="center">

  <img src="docs/assets/logo.png" alt="KheloJito Logo" width="110" style="border-radius: 24px; box-shadow: 0 8px 24px rgba(251, 191, 36, 0.4);" />

  # KheloJito Android
  
  **Official High-Performance Native Android Application for [KheloJito](https://khelojito.top/)**

  [![Android](https://img.shields.io/badge/Platform-Android_9.0+_API_28+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](#)
  [![Target SDK](https://img.shields.io/badge/Target_SDK-36-blue?style=for-the-badge&logo=android)](#)
  [![GPU 60 FPS](https://img.shields.io/badge/Engine-60+_FPS_Hardware_GPU-FBBF24?style=for-the-badge&logo=speedtest)](#)
  [![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)](#)

  <br />

  ### 🌐 Official Portals & Endpoints

  | Resource | URL |
  | :--- | :--- |
  | 🚀 **Official Landing Page** | [https://redzonerror.github.io/KheloJitoAndroid/](https://redzonerror.github.io/KheloJitoAndroid/) |
  | 🔗 **Dynamic Endpoint Feed** | [redzonerror.github.io/KheloJitoAndroid/link.txt](https://redzonerror.github.io/KheloJitoAndroid/link.txt) |
  | 🎮 **Live Web Platform** | [KheloJito](https://khelojito.top/) |

</div>

---

## 📖 Overview

**KheloJito Android** is an ultra-fast, GPU-accelerated gaming application built for real-money multiplayer games including **Teen Patti**, **Plane Crash**, **Ludo**, **Color Prediction**, and **Spin the Wheel**. 

The app wraps the high-performance React WebGL/Canvas game engine with native Android hardware optimizations, immersive edge-to-edge cut-out display support, and real-time remote server resolution.

---

## ⚡ Key Features

- **🚀 60+ FPS Hardware Acceleration**: Full GPU layer rendering (`View.LAYER_TYPE_HARDWARE` + `FLAG_HARDWARE_ACCELERATED`) ensures fluid card deals, table rotations, and rocket flight animations with 0ms touch delay.
- **🔄 Zero-Downtime Dynamic Endpoint Sync**: The app checks [link.txt](https://redzonerror.github.io/KheloJitoAndroid/link.txt) and the repository README on GitHub in real-time. If the domain or server mirror changes, all installed apps automatically update their connection without requiring a reinstall.
- **💾 Smart Caching & Instant Boot**: Pre-caches bundle assets locally in `khelojito_cache`. Reopens at 0ms local speed with graceful `LOAD_CACHE_ELSE_NETWORK` offline resilience.
- **📱 True Immersive Mode**: Hides system status and navigation bars with sticky immersive mode. Full display cut-out support (`LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES`) guarantees full-bleed gaming behind camera punch-holes.
- **🔊 Instant Sound Effects Autoplay**: Bypasses browser audio tap restrictions (`mediaPlaybackRequiresUserGesture = false`), allowing chip bets, blinds, folds, countdown ticks, and win fanfare to play automatically.
- **💸 Native UPI & Payout Interception**: Direct deep linking intercepts PhonePe, Google Pay, and Paytm payment intents for quick one-tap deposits and withdrawals.
- **📎 In-App Support Attachments**: Native file chooser integration (`onShowFileChooser`) enables players to upload screenshots and proof in Support Chat.

---

## 🎮 Featured Games

<div align="center">
  <table>
    <tr>
      <td align="center" width="20%">
        <img src="docs/assets/patti.png" width="70" /><br />
        <b>Teen Patti Live</b><br />
        <sub>Multiplayer 3-Card Poker</sub>
      </td>
      <td align="center" width="20%">
        <img src="docs/assets/plane.png" width="70" /><br />
        <b>Plane Crash</b><br />
        <sub>100x Rocket Multiplier</sub>
      </td>
      <td align="center" width="20%">
        <img src="docs/assets/ludo.png" width="70" /><br />
        <b>Ludo Battles</b><br />
        <sub>Real Money Board Game</sub>
      </td>
      <td align="center" width="20%">
        <img src="docs/assets/color.png" width="70" /><br />
        <b>Color Prediction</b><br />
        <sub>Fast 30s Win Rounds</sub>
      </td>
      <td align="center" width="20%">
        <img src="docs/assets/wheel.png" width="70" /><br />
        <b>Lucky Wheel</b><br />
        <sub>Instant High Multipliers</sub>
      </td>
    </tr>
  </table>
</div>

---

## 🛠️ Project Structure

```
Android/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml           # Permissions, Hardware Acceleration & Cutout config
│   │   ├── java/com/app/khelojito/
│   │   │   └── MainActivity.java         # Core GPU WebView, GitHub Link Sync & Native UI
│   │   └── res/
│   │       ├── layout/activity_main.xml  # Fullscreen WebView, splash loader & offline card
│   │       ├── values/themes.xml         # Fullscreen NoActionBar Dark Theme (#020617)
│   │       ├── values/colors.xml         # Branded Gold, Slate, and Obsidian Dark colors
│   │       └── mipmap-*/                 # Adaptive 108dp icons + legacy launcher icons
├── docs/                                 # GitHub Pages Static Website
│   ├── index.html                        # Responsive Gaming Showcase Landing Page
│   ├── style.css                         # Glassmorphic dark styling & micro-animations
│   ├── main.js                           # Real-time link.txt fetcher & interactive UI
│   ├── link.txt                          # Live server URL (Single Source of Truth)
│   └── assets/                           # 3D transparent game assets & logo
├── build.gradle                          # Root Gradle build script
└── README.md                             # Repository documentation & endpoints
```

---

## 🚀 Building the APK

### Prerequisites
- **Android Studio** Ladybug (2024.2.1+) or newer
- **JDK**: 11 or 17
- **Android SDK**: API 36 (Minimum API 28 / Android 9.0+)

### Build Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/RedZONERROR/KheloJitoAndroid.git
   cd KheloJitoAndroid
   ```

2. **Assemble the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   The compiled APK will be generated at:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License & Disclaimer

This project is licensed under the [MIT License](LICENSE).  
**18+ Only**: Please play responsibly. Real money gaming involves financial risk.

---

<div align="center">
  <sub>Maintained with ❤️ for the <b><a href="https://khelojito.top/">KheloJito</a></b> Community</sub>
</div>
