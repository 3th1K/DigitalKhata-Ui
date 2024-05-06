package com.example.digitalkhata.util

import android.content.Context
import com.example.digitalkhata.model.UserResponse

object LocalStorage {

    private const val PREFS_NAME = "AppPrefs"

    fun saveUserDetails(context: Context, user : UserResponse) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("userId", user.userId.toString())
        editor.putString("userName", user.username)
        editor.putString("fullName", user.fullname)
        editor.putString("email", user.email)
        editor.apply()
    }

    fun getUserId(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString("userId", null)
    }

    fun getUserName(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString("userName", null)
    }

    fun getFullName(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString("fullName", null)
    }

    fun getEmail(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString("email", null)
    }


    fun saveAuthToken(context: Context, token: String) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("authToken", token)
        editor.apply()
    }

    fun getAuthToken(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString("authToken", null)
    }

    fun clearUserData(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.remove("userId")
        editor.remove("userName")
        editor.remove("fullName")
        editor.remove("email")
        editor.apply()
    }

    fun clearAuthToken(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.remove("authToken")
        editor.apply()
    }

    fun clearAllData(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }
}
