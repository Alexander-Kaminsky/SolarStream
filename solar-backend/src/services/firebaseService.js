const admin = require('firebase-admin');
const config = require('../config');
const serviceAccount = require('../config/serviceAccountKey.json');

// Initialize once
if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        // Make sure your config.js maps config.firebaseUrl to process.env.FIREBASE_DATABASE_URL
        databaseURL: config.firebaseUrl
    });
}

const db = admin.database();

// Updated to match the Android App's Telemetry.kt model
const saveTelemetry = async (data) => {
    const { solarAmps, chargeAmps, loadAmps, batteryPercent, busVoltage } = data;

    // We save to 'telemetry/current' for the dashboard to read quickly
    const ref = db.ref(`telemetry/current`);

    return ref.set({
        solarAmps: parseFloat(solarAmps) || 0.0,
        chargeAmps: parseFloat(chargeAmps) || 0.0,
        loadAmps: parseFloat(loadAmps) || 0.0,
        batteryPercent: parseInt(batteryPercent) || 0,
        busVoltage: parseFloat(busVoltage) || 0.0,
        timestamp: admin.database.ServerValue.TIMESTAMP
    });
};

const getLatestTelemetry = async () => {
    const ref = db.ref('telemetry/current');
    const snapshot = await ref.once('value');
    return snapshot.val();
};
// --- AUTHENTICATION ---
const authenticateUser = async (email, password) => {
    // A pragmatic mock-auth for the prototype.
    // It creates the user in the DB if they don't exist, or logs them in if they do.
    const userPrefix = email.split('@')[0].charAt(0).toUpperCase() + email.split('@')[0].slice(1);
    const userRef = db.ref(`users/${userPrefix}`);

    const snapshot = await userRef.once('value');
    if (!snapshot.exists()) {
        // Register new user
        await userRef.set({ email, created_at: admin.database.ServerValue.TIMESTAMP });
        return { status: 'registered', userPrefix };
    }
    // "Log in" existing user
    return { status: 'logged_in', userPrefix };
};

// --- CHAT SYSTEM ---
const saveChatMessage = async (userPrefix, text) => {
    const chatRef = db.ref('community_chat');
    const newMessageRef = chatRef.push(); // Generates unique ID

    const messageData = {
        userPrefix,
        text,
        timestamp: admin.database.ServerValue.TIMESTAMP
    };

    await newMessageRef.set(messageData);
    return messageData;
};

const getRecentChats = async (limit = 50) => {
    const chatRef = db.ref('community_chat');
    const snapshot = await chatRef.orderByChild('timestamp').limitToLast(limit).once('value');

    const messages = [];
    snapshot.forEach((child) => {
        messages.push({ id: child.key, ...child.val() });
    });
    return messages;
};

// CRITICAL: Export `db` so the HardwareSimulator can use it in app.js
module.exports = {
    saveTelemetry,
    getLatestTelemetry,
    authenticateUser,
    saveChatMessage,
    getRecentChats,
    db
};