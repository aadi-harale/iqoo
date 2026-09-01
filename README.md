<div align="center">

# 🛡️ iQOO Guardian

### Your phone already has the signals. Guardian turns them into intelligence.

**A phone-native, privacy-first intelligence layer for Android that learns normal device behaviour, correlates signals, predicts problems, explains why they are happening, and recommends the safest next action — locally.**

`MONITOR → BASELINE → CORRELATE → PREDICT → EXPLAIN → PROTECT`

</div>

---

## 🚀 What Guardian Is

Most phone utilities show isolated numbers: battery %, thermal state, storage used, app usage, notifications, network status. The user still has to figure out what those numbers mean together.

**iQOO Guardian** is designed as an **On-Device Device Intelligence Layer** that connects those signals into one incident and answers four questions:

- **What changed?**
- **Why does it matter?**
- **What happens next?**
- **What should I do?**

Guardian is not another RAM cleaner, antivirus clone, or generic chatbot. The core idea is simple:

> **Individual signals are weak. Correlated signals become intelligence.**

---

## 📸 Current Prototype

![Home Dashboard](docs/1_home.png)
![Device Digital Twin](docs/2_device.png)
![Demo Lab](docs/3_demolab.png)
![Live Threat Analysis](docs/4_analysis.png)

---

# 🧠 Core Intelligence

## 1. Behavioral Twin — "Normal for this phone"

Instead of relying only on generic thresholds, Guardian builds a lightweight **Behavioral Twin** from the phone's own historical behaviour.

Example baseline dimensions:

- battery drain rate
- thermal pressure / thermal status
- memory pressure
- storage growth
- app foreground/background activity
- notification patterns

This allows Guardian to detect **deviation from this device's normal state**, not just whether one metric crossed a fixed threshold.

Example:

```text
Normal background activity: 18–27%
FlashDeals today: +83% vs baseline
Battery drain: +34% vs baseline
Thermal pressure: rising
Memory pressure: rising
```

Guardian can then treat those shifts as one correlated incident instead of four unrelated warnings.

---

## 2. Contradiction Engine — Verify claims against the phone itself

Traditional security asks:

> "Is this message or app on a known threat list?"

Guardian can ask:

> "Does this claim match what the phone itself reports?"

### Hero scenario

```text
Notification:
"Storage critically low — under 1 GB remaining. Clean now."

Guardian measures:
42 GB free storage

Additional evidence:
- urgency language
- external link
- no supporting system state
```

Guardian combines the contradiction with supporting signals to generate an explainable risk assessment.

```text
CRITICAL RISK — 94 / 100
Reason: notification claim contradicts measured device state
Recommended action: do not open link · dismiss · inspect source app
```

---

## 3. Evidence Graph — Every verdict is inspectable

Guardian should never behave like a black-box "AI says so" system.

Every conclusion can be represented as an **Evidence Graph**:

```text
FlashDeals background activity +83%     [LIVE / DERIVED]
                ↓
Battery drain +34%                       [DERIVED]
                ↓
Memory pressure ↑                        [LIVE]
                ↓
Thermal pressure ↑                       [LIVE]
                ↓
Performance degradation likely          [PREDICTED]
```

Each signal keeps provenance such as:

- `LIVE` — measured directly from Android
- `DERIVED` — computed from measured data
- `PREDICTED` — forecast from deterministic logic
- `DEMO` — intentionally simulated for presentation

This makes the system explainable without exposing raw model chain-of-thought.

---

## 4. Why Now? Timeline — Explain how an incident formed

Guardian reconstructs the order in which related signals changed.

Example:

```text
07:42  FlashDeals background activity rises
07:53  Battery drain moves outside baseline
08:01  Memory pressure increases
08:08  Thermal pressure increases
08:16  Guardian predicts slowdown
```

The timeline shows **correlated evidence over time**, helping the user understand why the alert appeared now.

---

## 5. Counterfactual Simulator — "What happens if I do this?"

Guardian does not stop at diagnosis. It simulates likely outcomes before the user acts.

Example:

```text
Do nothing
→ Battery reaches 20% at 8:40 PM

Restrict FlashDeals
→ Battery reaches 20% at 10:05 PM
→ Estimated gain: +1h 25m
```

Possible future action comparisons can include:

- restrict abnormal background activity
- reduce expensive device behaviour
- leave the device unchanged

This turns Guardian from a passive monitor into a **decision-support system**.

---

## 6. Adaptive Intelligence — Guardian regulates its own AI cost

A unique phone-first requirement: the AI itself must not become the source of battery drain or thermal pressure.

Guardian's **Adaptive Intelligence policy** changes inference behaviour based on device state:

| Device state | Guardian behaviour |
|---|---|
| Cool + sufficiently charged | Full local explanation / richer reasoning |
| Thermal pressure rising | Reduce inference frequency |
| Low battery | Prefer deterministic engines, minimal LLM use |
| Stable again | Resume richer local reasoning |

> **Guardian does not only understand the cost of other apps — it understands the cost of running its own AI.**

---

# 🤖 Real Local AI Layer

Guardian's critical findings remain deterministic. The language model is used for **grounded explanation**, not for deciding whether something is dangerous.

### Development path

```text
Ollama
  ↓
Qwen2.5 1.5B Instruct
  ↓
Structured DEVICE_CONTEXT
  ↓
Grounded natural-language explanation
```

### Android target

```text
Qwen2.5 1.5B Instruct
        ↓
Quantized GGUF
        ↓
llama.cpp Android runtime
        ↓
On-device inference
```

### Model safety contract

The model must:

1. use only facts present in `DEVICE_CONTEXT`
2. never invent measurements or findings
3. explicitly say when evidence is insufficient
4. return structured explanations only
5. never perform destructive operations

The model is the **narrator, not the analyst**. If the model is unavailable, Guardian should still detect, correlate, score and predict.

---

# ⚙️ Intelligence Pipeline

```text
ANDROID SIGNALS
      ↓
Signal Normalizer
      ↓
Device Snapshot
      ↓
Behavioral Twin / Personal Baseline
      ↓
Anomaly Detection
      ↓
Context Correlation
      ↓
Contradiction / Risk Engine
      ↓
Prediction + Counterfactual Engine
      ↓
Evidence Graph + Why Now Timeline
      ↓
Local LLM Explanation
      ↓
Guardian Event / Recommended Action
```

Core deterministic engines:

- **Baseline Engine** — learns normal device behaviour
- **Anomaly Engine** — detects deviations
- **Context Correlation Engine** — groups related shifts
- **Contradiction Engine** — compares external claims against verified device state
- **Risk Engine** — produces deterministic severity scores
- **Prediction Engine** — forecasts likely impact
- **Counterfactual Engine** — compares likely outcomes of different actions
- **Adaptive Intelligence Policy** — controls local inference depth and frequency

---

# 📱 Guardian Experience

## Guardian Core
A live summary of device posture, active incidents, predictions and current Guardian intelligence mode.

## Device Digital Twin
A visual map linking battery, thermal, memory, storage, apps, privacy, performance and other supported sectors into one connected device state.

## Guardian Investigate
Runs the full chain on demand:

```text
Collect → Compare Baseline → Check Contradictions → Correlate → Predict → Explain
```

## Guardian Ask
Grounded local Q&A for questions such as:

- Why is my phone heating?
- Why is my battery draining?
- Is this notification safe?
- What changed today?
- What happens if I restrict this app?

Answers are generated only from measured or computed Guardian context.

## What Changed
A timeline of meaningful device-state changes during the day.

## Incident Replay
Timestamped reconstruction of how an incident developed.

## Offline Proof
Visible proof that the Guardian decision path works without a cloud backend.

## Presentation Mode
Deterministic demo reset so judges always see the same reliable scenario.

---

# 🔒 Privacy & Technical Honesty

Guardian is designed around legitimate Android-accessible signals and explicit provenance.

It does **not** claim that a normal third-party Android app can see every process, every message, every file or privileged OS telemetry.

Where Android restricts access, Guardian should:

- degrade gracefully
- show the missing permission/signal
- avoid inventing values
- distinguish real device telemetry from demo data

The long-term vision is deeper OEM integration, where Guardian could become a first-party intelligence layer inside the operating system.

---

# 🛠️ Android Stack

Current / planned architecture:

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **MVVM**
- **Coroutines / Flow / StateFlow**
- **Room**
- **DataStore**
- **WorkManager**
- Android system APIs such as BatteryManager, UsageStatsManager and supported thermal/storage/notification APIs
- **Ollama** for local-model development and prompt iteration
- **Qwen2.5 1.5B Instruct**
- **llama.cpp + quantized GGUF** as the Android on-device model path

---

# 🎯 Phase 1 Build Priorities

The goal is not to ship every possible device-health feature. The goal is to prove one memorable vertical slice end-to-end.

### P0 — Must work

1. Behavioral Twin
2. Fake-storage Contradiction Engine demo
3. Evidence Graph
4. Why Now? timeline
5. Counterfactual Simulator
6. Adaptive Intelligence
7. Grounded local-model explanation
8. Offline Proof
9. Deterministic Presentation Mode

### P1 — Supporting experience

- Guardian Ask
- Device Digital Twin
- What Changed
- Incident Replay
- Guardian Score breakdown

### P2 — Future / OEM integration

- deeper first-party OS hooks
- broader supported signal set
- richer on-device model execution
- system-level action routing

---

# 🎬 Demo Story

The ideal demo should be understandable in under 90 seconds:

```text
1. Open Guardian Core
2. Show Behavioral Twin / current normal state
3. Trigger fake storage warning
4. Guardian reads actual free storage
5. Contradiction detected
6. Evidence Graph appears
7. Why Now? timeline reconstructs the incident
8. Risk score becomes CRITICAL
9. Counterfactual Simulator compares actions
10. Local model explains the finding
11. Adaptive Intelligence shows current AI mode
12. Offline Proof confirms local processing
```

---

# 🚀 Running Presentation Mode

To run the automated demo flow:

1. Connect an Android device over USB.
2. Enable Developer Options and USB Debugging.
3. Open PowerShell in the project root.
4. Run:

```powershell
.\Presentation.ps1
```

The script can drive the judge-demo sequence using ADB/deep links where configured.

---

<div align="center">

## The Product Principle

**Guardian learns what is normal for your phone, proves what changed, predicts what happens next, simulates what to do about it, and adapts the cost of its own AI — locally.**

*Built for iQOO Hackathon 2026 · Productivity Track*

</div>
