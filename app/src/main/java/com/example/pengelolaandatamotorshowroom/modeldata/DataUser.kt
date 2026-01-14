package com.example.pengelolaandatamotorshowroom.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class DataUser(
    val id: Int,
    val nama_lengkap: String,
    val email: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: DataUser? = null
)