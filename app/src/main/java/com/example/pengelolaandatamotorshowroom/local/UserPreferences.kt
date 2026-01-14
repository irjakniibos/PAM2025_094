package com.example.pengelolaandatamotorshowroom.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "showroom_motor_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUserData(token: String, userId: Int, userName: String, userEmail: String) {
        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, "Bearer $token")
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, userEmail)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, 0)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}