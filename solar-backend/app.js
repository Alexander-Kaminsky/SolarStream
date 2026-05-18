const express = require('express');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const path = require('path');
const config = require('./src/config');
const logger = require('./src/middleware/logger');
const telemetryRoutes = require('./src/routes/telemetryRoutes');
const authRoutes = require('./src/routes/authRoutes'); // NEW
const chatRoutes = require('./src/routes/chatRoutes'); // NEW
// Ensure firebaseService.js exports your admin.database() instance
const { db } = require('./src/services/firebaseService');

const app = express();

const swaggerDocument = YAML.load('./docs/swagger.yaml');

app.use(express.json());

app.use('/api/v1', telemetryRoutes);
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/chat', chatRoutes);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

app.get('/health', (req, res) => {
    res.status(200).json({ status: 'UP', timestamp: new Date().toISOString() });
});

// --- HARDWARE SIMULATION INJECTION ---
if (process.env.SIMULATE_HARDWARE === 'true') {
    const HardwareSimulator = require('./src/services/HardwareSimulator');

    const simulator = new HardwareSimulator(db);
    simulator.startSimulation();

    // Optional: Log it via your custom logger
    // logger.info("Hardware Simulator started in background.");
}

module.exports = app;