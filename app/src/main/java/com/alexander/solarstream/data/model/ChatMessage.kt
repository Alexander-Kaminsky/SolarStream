package com.alexander.solarstream.data.model

data class ChatMessage(
    val userPrefix: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)