const firebaseService = require('../services/firebaseService');
const logger = require('../middleware/logger');

exports.uploadData = async (req, res) => {
    try {
        const { solarAmps, chargeAmps, loadAmps, batteryPercent, busVoltage } = req.body;

        // Validation: Ensuring the core battery metrics are present.
        // Amps can technically be 0 if the system is idle, but voltage/percent should always exist.
        if (batteryPercent === undefined || busVoltage === undefined) {
            return res.status(400).json({
                error: 'Missing required telemetry fields. Expected: solarAmps, chargeAmps, loadAmps, batteryPercent, busVoltage'
            });
        }

        await firebaseService.saveTelemetry({
            solarAmps,
            chargeAmps,
            loadAmps,
            batteryPercent,
            busVoltage
        });

        logger.info(`Telemetry data successfully synced to Firebase.`);
        res.status(200).json({ status: 'success', message: 'Telemetry recorded' });

    } catch (error) {
        logger.error(`Controller Error [POST]: ${error.message}`);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

exports.getLatestData = async (req, res) => {
    try {
        const data = await firebaseService.getLatestTelemetry();

        // Fallback for an empty database to prevent Android parsing errors
        if (!data) {
            return res.status(200).json({
                solarAmps: 0.0,
                chargeAmps: 0.0,
                loadAmps: 0.0,
                batteryPercent: 0,
                busVoltage: 0.0
            });
        }

        // Return the full telemetry object
        res.status(200).json({
            solarAmps: data.solarAmps || 0.0,
            chargeAmps: data.chargeAmps || 0.0,
            loadAmps: data.loadAmps || 0.0,
            batteryPercent: data.batteryPercent || 0,
            busVoltage: data.busVoltage || 0.0,
            timestamp: data.timestamp
        });
    } catch (error) {
        logger.error(`Controller Error [GET]: ${error.message}`);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};