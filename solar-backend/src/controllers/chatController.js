const firebaseService = require('../services/firebaseService');
const logger = require('../middleware/logger');

exports.postMessage = async (req, res) => {
    try {
        const { userPrefix, text } = req.body;

        if (!userPrefix || !text) {
            return res.status(400).json({ error: 'userPrefix and text are required' });
        }

        const savedMessage = await firebaseService.saveChatMessage(userPrefix, text);
        res.status(201).json({ status: 'success', data: savedMessage });

    } catch (error) {
        logger.error(`Chat Post Error: ${error.message}`);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

exports.getMessages = async (req, res) => {
    try {
        const messages = await firebaseService.getRecentChats();
        res.status(200).json({ status: 'success', count: messages.length, data: messages });
    } catch (error) {
        logger.error(`Chat Get Error: ${error.message}`);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};