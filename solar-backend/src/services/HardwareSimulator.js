const admin = require('firebase-admin');

class HardwareSimulator {
    constructor(db) {
        this.db = db;
        this.batteryPercent = 85;
        this.intervalId = null;

        // State Machine Tracking
        this.tickCount = 0;
        this.weatherState = 'SUNNY';
    }

    startSimulation() {
        console.log("[ESP32 Simulator] Started. Pushing data every 5 seconds.");
        this.intervalId = setInterval(() => this.generateAndPushTelemetry(), 5000);
    }

    stopSimulation() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
            console.log("[ESP32 Simulator] Stopped.");
        }
    }

    generateAndPushTelemetry() {
        this.tickCount++;

        // Cycle the state every 3 ticks (15 seconds per state)
        if (this.tickCount > 3) {
            this.tickCount = 1;
            if (this.weatherState === 'SUNNY') this.weatherState = 'CLOUDY';
            else if (this.weatherState === 'CLOUDY') this.weatherState = 'IDLE';
            else this.weatherState = 'SUNNY';

            console.log(`[ESP32 Simulator] Weather Shifted to: ${this.weatherState}`);
        }

        let solarAmps, chargeAmps, loadAmps;

        // Apply realistic physics based on the current weather state
        if (this.weatherState === 'SUNNY') {
            solarAmps = (Math.random() * (4.8 - 4.0) + 4.0).toFixed(2); // ~4.5A from panel
            chargeAmps = (solarAmps * 0.9).toFixed(2); // Account for controller efficiency
            loadAmps = (Math.random() * (1.2 - 0.8) + 0.8).toFixed(2); // Normal load
        } else if (this.weatherState === 'CLOUDY') {
            solarAmps = (Math.random() * (0.8 - 0.3) + 0.3).toFixed(2); // Weak panel output
            chargeAmps = 0.00; // Voltage too low to charge
            loadAmps = (Math.random() * (2.2 - 1.8) + 1.8).toFixed(2); // Spiked load
        } else { // IDLE (Night time)
            solarAmps = 0.00;
            chargeAmps = 0.00;
            loadAmps = 0.00; // System resting
        }

        // Calculate net flow to determine if battery gains or loses percent
        const netFlow = parseFloat(chargeAmps) - parseFloat(loadAmps);

        if (netFlow > 0 && this.batteryPercent < 100) {
            this.batteryPercent += 1;
        } else if (netFlow < 0 && this.batteryPercent > 0) {
            this.batteryPercent -= 1;
        }

        // Map percentage to a realistic Li-ion bus voltage (3.0V to 4.2V)
        const currentVoltage = (3.0 + (this.batteryPercent / 100) * 1.2).toFixed(2);

        const telemetryPayload = {
            solarAmps: parseFloat(solarAmps),
            chargeAmps: parseFloat(chargeAmps),
            loadAmps: parseFloat(loadAmps),
            batteryPercent: this.batteryPercent,
            busVoltage: parseFloat(currentVoltage),
            timestamp: admin.database.ServerValue.TIMESTAMP
        };

        this.db.ref('telemetry/current').set(telemetryPayload)
            .then(() => console.log(`[ESP32 Simulator] Pushed: Battery at ${this.batteryPercent}% | Net Flow: ${netFlow.toFixed(2)}A`))
            .catch(err => console.error("[ESP32 Simulator] Error pushing data:", err));
    }
}

module.exports = HardwareSimulator;