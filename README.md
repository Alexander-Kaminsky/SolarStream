# SunStream ☀️🔋
**Visualize Your Power. Share Your Grid.**

SunStream is a full-stack, real-time IoT telemetry dashboard and community platform designed for DIY solar and hardware builders. It visualizes live power flow from hardware sensors (simulated ESP32/CN3791) and provides an interactive, real-time community feed.

## 🏗 Architecture & Tech Stack

This project strictly adheres to clean architecture guidelines, separating UI, business logic, and data layers, heavily utilizing **Jetpack Compose** and modern Android development practices.

**Frontend (Android / Kotlin):**
* **UI Toolkit:** Jetpack Compose (Material 3).
* **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow.
* **State Management:** `StateFlow` and `MutableStateFlow` with strict State Hoisting to ensure stateless, highly testable Composable functions.
* **Networking:** Retrofit2 & Gson (for REST API authentication).
* **Navigation:** Compose Navigation (`NavHost`) with an extracted Scaffold bottom bar.
* **Visuals:** Lottie animations for dynamic, real-time hardware state representation.

**Backend & Infrastructure:**
* **Server:** Node.js / Express.js REST API (`/api/v1/auth`).
* **Database:** Firebase Realtime Database (utilized directly via WebSockets for zero-latency telemetry and chat updates).
* **Hardware Simulation:** A custom backend State Machine simulator mimicking an ESP32 publishing telemetry from a solar charge controller and a 1S8P 18650 battery bank.

## ✨ Core Features & Functions

### 1. Real-Time Telemetry Dashboard
* **WebSocket Integration:** Bypasses HTTP polling by maintaining an open WebSocket connection to Firebase, ensuring sub-second UI updates.
* **Reactive Animations:** Lottie components dynamically transition states (Charging, Discharging, Idle) based on the calculated Net-Flow (Solar Input minus System Load).
* **Hardware State Machine:** The Node.js simulator loops through `SUNNY`, `CLOUDY`, and `IDLE` states, applying realistic physics to voltage constraints and battery percentage limits.

### 2. Interactive Community Feed
* **Optimized Rendering:** Built utilizing Compose's `LazyColumn` for high-performance recycling of feed elements.
* **NoSQL Map Schema (Likes):** The Like system prevents duplicate interactions by utilizing a production-ready NoSQL map schema (`liked_by: { userId: true }`) rather than a simple integer incrementer.
* **CRUD Operations:** Users can dynamically create posts via a Material Extended FAB, and delete posts using a robust internal state check (`isMyPost = post.userPrefix == currentUser`) that dynamically mounts/unmounts the deletion UI.

### 3. Authentication & Security
* **Client-Side Validation:** Utilizes local Regex logic to validate inputs prior to network execution, preventing unnecessary API load.
* **REST API:** Communicates with the Node.js backend using Retrofit to handle mock authentication, resolving the user prefix for database tracking.
* **Singleton Session Management:** A `SessionManager` utilizes the Singleton design pattern to persist user identity across the application lifecycle without memory leaks.
