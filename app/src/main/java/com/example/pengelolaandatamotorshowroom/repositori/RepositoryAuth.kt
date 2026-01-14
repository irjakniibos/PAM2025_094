package com.example.pengelolaandatamotorshowroom.repositori

import com.example.pengelolaandatamotorshowroom.apiservice.ServiceApiShowroom
import com.example.pengelolaandatamotorshowroom.local.UserPreferences
import com.example.pengelolaandatamotorshowroom.modeldata.LoginRequest
import com.example.pengelolaandatamotorshowroom.modeldata.LoginResponse

class RepositoryAuth(
    private val api: ServiceApiShowroom,
    private val userPreferences: UserPreferences
) {
    suspend fun login(email: String, password: String): LoginResponse {
        val response = api.login(LoginRequest(email, password))
        if (response.success && response.token != null && response.user != null) {
            userPreferences.saveUserData(
                token = response.token,
                userId = response.user.id,
                userName = response.user.nama_lengkap,
                userEmail = response.user.email
            )
        }
        return response
    }

    suspend fun logout() {
        val token = userPreferences.getToken()
        if (token != null) {
            try {
                api.logout(token)
            } catch (e: Exception) {
                // Ignore error, clear local data anyway
            }
        }
        userPreferences.clearUserData()
    }

    fun getToken(): String? {
        return userPreferences.getToken()
    }

    fun isLoggedIn(): Boolean {
        return userPreferences.isLoggedIn()
    }
}