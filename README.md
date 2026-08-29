# iQOO Guardian 🛡️

> **"Your phone understands threats before you do."**

iQOO Guardian is an **On-Device Intelligence Layer** built for Android. Rather than functioning as a reactive, cloud-dependent antivirus or a generic "RAM booster," Guardian acts as a biological immune system for your device. It continuously monitors physical and software telemetry—and correlates those signals in real-time to detect anomalous behavior.

---

## ✨ Why Guardian? (The Core Innovations)

### 1. The Contradiction Engine 🧠
Traditional security asks: *"Is this app on a known malware list?"* 
Guardian asks: *"This app claims to be a simple calculator, but it is currently draining 30% of the battery, spiking the CPU temp by 5°C, and making 50 background network requests. This is a **contradiction**."*

The engine correlates disjointed hardware signals into a unified risk score, mapped visually on the **Context Correlation Graph** (`APP ACTIVITY -> THERMAL -> BATTERY -> RISK VERDICT`) so users can understand *exactly* why an app was flagged.

### 2. 100% On-Device Processing 🔒
Privacy is the ultimate feature. Because Guardian relies on hardware telemetry rather than cloud-based signature matching, it functions entirely offline. The built-in **Privacy Center** features an *Offline Proof Mode* that explicitly verifies 0 bytes are being uploaded to the cloud during analysis.

### 3. Device Digital Twin UI 📱
Forget walls of text. Guardian features a **Device Digital Twin**—a pulsing, animated representation of the phone's current intelligence state. It is surrounded by a highly polished **Bento Grid** containing 10 deeply interactive sector pages:
- **Performance & Memory:** Waveforms and gauges tracking system load.
- **Thermal & Battery:** Radar sweeps and discharging arcs showing energy pressure.
- **Storage, Network, Camera, Sensors, Health:** Hardware availability diagnostics.

### 4. Predictive Impact (Counterfactuals) 🔮
Guardian doesn't just report threats; it predicts the future. If a threat is found, the system generates a counterfactual impact statement: *"If you restrict FlashDeals now, you will regain +1h 25m of battery life."* This transforms passive reporting into immediate, actionable value.

---

## 🛠️ Technical Implementation

- **Jetpack Compose Architecture:** Over 15 distinct screens built completely without XML. Every UI element is written in declarative Kotlin, utilizing custom `Canvas` drawing for radars, digital twins, and energy arcs.
- **Resilient Coroutine Navigation:** Engineered a highly resilient navigation architecture using `GlobalScope` coroutines to drive a fully automated "Ghost Touch" autopilot presentation mode.
- **State-Driven Animation Engine:** Infinite transitions, glitch effects for critical threats, and staggered entrance animations ensure the UI doesn't just load—it *assembles* itself smoothly.
- **Deep-Linked ADB Automation:** Features a dedicated PowerShell script (`Presentation.ps1`) that interfaces with Android `adb shell` to fire deep-links and simulate physical finger swipes (`input swipe`). This allows the phone to autonomously present itself to judges without any human interaction.

---

## 🚀 Running the Presentation Mode

To watch Guardian present itself autonomously:
1. Connect your Android device via USB.
2. Ensure Developer Options and USB Debugging are enabled.
3. Open a PowerShell terminal in the project root.
4. Run:
   ```powershell
   .\Presentation.ps1
   ```
Sit back and watch the ghost-touch automation guide you through the Digital Twin, Sector deeply dives, Privacy offline proofing, and the Demo Lab threat simulation.

---

*Built for Hackathons. Engineered for the future of on-device AI.*
