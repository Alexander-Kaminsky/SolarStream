const firebaseService = require('../services/firebaseService');
const logger = require('../middleware/logger');

exports.login = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ error: 'Email and password are required' });
        }

        const result = await firebaseService.authenticateUser(email, password);

        logger.info(`User authenticated: ${result.userPrefix}`);
        res.status(200).json({
            message: 'Authentication successful',
            userPrefix: result.userPrefix,
            status: result.status
        });

    } catch (error) {
        logger.error(`Auth Error: ${error.message}`);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};