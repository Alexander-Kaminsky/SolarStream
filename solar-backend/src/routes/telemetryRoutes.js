const express = require('express');
const router = express.Router();
const telemetryController = require('../controllers/telemetryController');

router.post('/telemetry', telemetryController.uploadData);
router.get('/telemetry', telemetryController.getLatestData);
module.exports = router;