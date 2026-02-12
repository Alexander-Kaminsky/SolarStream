Here is a professional README designed for your GitHub, written in your voice and tailored for the Yazamco interview. It explains the "Tachles" of how you built this full-stack IoT ecosystem in record time.

☀️ SolarStream IoT
A full-stack monitoring system for a portable solar power station, built as a Rapid Prototype to demonstrate integration between hardware telemetry, cloud middleware, and reactive mobile UI.

🏗️ The Ecosystem
I built this project to prove a "Real-time Data Pipeline" from the sun to your phone:

Hardware Layer: An ESP32 reads voltage and current from a CN3791 MPPT solar controller and a 4-cell parallel 18650 battery nest.

Middleware (Node.js): Acts as a Secure Gateway. It protects the ESP32 (which is "unprotected" on its own) and handles data validation before pushing to the cloud.

Cloud (Firebase): Used as the Source of Truth for real-time data persistence and synchronization.

Mobile App (Android): A modern Jetpack Compose dashboard that reacts to the telemetry state with "Glassmorphism" animations.

🛠️ Tech Stack & Architecture
Backend: Node.js, Express, Swagger (OAS 3.0) for API documentation.

Mobile: Kotlin, Jetpack Compose, MVVM Architecture, Coroutines for async networking.

Design Principles: Applied 12-Factor App methodology, specifically Factor VII (Port Binding) and Factor III (Config) for environment-based scalability.

🚀 Key Features
Real-time Sync: Hit "Sync Cloud" to pull live telemetry via my Node.js API.

Dynamic UI: The battery level fills based on Voltage-to-SoC mapping (

4.2V
peak).

Charging Animations: The PowerFlowLine and ChargingPulse only trigger when the system detects active current from the panels.

Simulate Mode: Includes a "Cloud Cover" simulation to test UI edge cases when offline.

📋 How to Run
Backend: cd solar-backend -> npm install -> npm start.

Android: Open in Android Studio and ensure the base_url in the ViewModel matches your local IP.
