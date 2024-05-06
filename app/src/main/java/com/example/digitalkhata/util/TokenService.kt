package com.example.digitalkhata.util

import android.content.Context
import com.auth0.jwt.JWT
import java.util.Date

object TokenService {

    fun isUserLoggedIn(context: Context): Boolean {
        // Check if token is present
        val authToken = LocalStorage.getAuthToken(context)
        if (authToken.isNullOrEmpty()) {
            return false
        }

        // Check if token is valid
        if (!isTokenValid(authToken)) {
            // Token is invalid, clear it
            LocalStorage.clearAuthToken(context)
            return false
        }

        // Check if user data is present
        val userId = LocalStorage.getUserId(context)
        return !userId.isNullOrEmpty()
    }

    // Add function to validate token
    private fun isTokenValid(token: String): Boolean {
        try {
            val decodedToken = JWT.decode(token)
            val expiryDateSeconds = decodedToken.expiresAt?.time ?: 0
            val currentDateTimeSeconds = Date().time / 1000 // Convert milliseconds to seconds
            return (expiryDateSeconds > currentDateTimeSeconds)
        } catch (e: Exception) {
            return false
        }
    }
}
