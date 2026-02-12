const admin = require('firebase-admin');
const config = require('../config');
const serviceAccount = require('../config/serviceAccountKey.json');

// Initialize once
if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        databaseURL: config.firebaseUrl
    });
}

const db = admin.database();

const saveTelemetry = async (data) => {
    const { deviceId, voltage, current } = data;
    // We save to a "live" node for the dashboard to read quickly
    const ref = db.ref(`telemetry/latest`);

    return ref.set({
        deviceId,
        voltage: parseFloat(voltage),
        current: parseFloat(current),
        timestamp: admin.database.ServerValue.TIMESTAMP
    });
};

const getLatestTelemetry = async () => {
    const ref = db.ref('telemetry/latest');
    const snapshot = await ref.once('value');
    return snapshot.val();
};

module.exports = { saveTelemetry, getLatestTelemetry };