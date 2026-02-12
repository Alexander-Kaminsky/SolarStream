const firebaseService = require('../services/firebaseService');
const logger = require('../middleware/logger');

exports.uploadData = async (req, res) => {
    try {
        const { deviceId, voltage, current } = req.body;

        if (!deviceId || voltage === undefined || current === undefined) {
            return res.status(400).json({ error: 'Missing required fields: deviceId, voltage, current' });
        }

        await firebaseService.saveTelemetry({ deviceId, voltage, current });

        logger.info(`Data synced for device: ${deviceId}`);
        res.status(200).json({ status: 'success', message: 'Telemetry recorded' });

    } catch (error) {
        logger.error(`Controller Error: ${error.message}`);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

exports.getLatestData = async (req, res) => {
    try {
        const data = await firebaseService.getLatestTelemetry();

        if (!data) {
            return res.status(200).json({ voltage: 0.0, current: 0.0 });
        }

        res.status(200).json({
            voltage: data.voltage,
            current: data.current
        });
    } catch (error) {
        console.error("GET Error:", error);
        res.status(500).json({ error: error.message });
    }
};