require('dotenv').config();

module.exports = {
    port: process.env.PORT || 3000,
    firebaseUrl: process.env.FIREBASE_DATABASE_URL,
    logLevel: process.env.LOG_LEVEL || 'info'
};