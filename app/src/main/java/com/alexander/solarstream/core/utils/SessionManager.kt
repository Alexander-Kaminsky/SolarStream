package com.alexander.solarstream.core.utils

/*
 * Holds the current user's session data in memory.
 */
class SessionManager private constructor() {

    var currentUserPrefix: String = "Guest"
        private set

    // Pragmatic logic: Extracts "alex" from "alex@gmail.com"
    fun loginUser(email: String) {
        currentUserPrefix = email.substringBefore("@").replaceFirstChar { it.uppercase() }
    }

    fun logout() {
        currentUserPrefix = "Guest"
    }

    companion object {
        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager().also { instance = it }
            }
        }
    }
}