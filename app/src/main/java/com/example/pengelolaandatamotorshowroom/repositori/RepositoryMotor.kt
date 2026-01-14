package com.example.pengelolaandatamotorshowroom.repositori

import com.example.pengelolaandatamotorshowroom.apiservice.ServiceApiShowroom
import com.example.pengelolaandatamotorshowroom.local.UserPreferences
import com.example.pengelolaandatamotorshowroom.modeldata.*

class RepositoryMotor(
    private val api: ServiceApiShowroom,
    private val userPreferences: UserPreferences
) {
    private fun getToken(): String {
        return userPreferences.getToken() ?: throw Exception("Token tidak ditemukan")
    }

    suspend fun getMotorsByBrand(brandId: Int): List<DataMotor> {
        val token = getToken()
        val response = api.getMotorsByBrand(token, brandId)
        return response.data
    }

    suspend fun getMotorDetail(id: Int): DataMotor {
        val token = getToken()
        val response = api.getMotorDetail(token, id)
        return response.data
    }

    suspend fun createMotor(
        namaMotor: String,
        brandId: Int,
        tipe: String,
        tahun: Int,
        harga: Double,
        warna: String,
        stokAwal: Int
    ): ResponseApi<DataMotor> {
        val token = getToken()
        return api.createMotor(
            token,
            MotorRequest(namaMotor, brandId, tipe, tahun, harga, warna, stokAwal)
        )
    }

    suspend fun updateMotor(
        id: Int,
        namaMotor: String,
        brandId: Int,
        tipe: String,
        tahun: Int,
        harga: Double,
        warna: String
    ): ResponseApi<DataMotor> {
        val token = getToken()
        return api.updateMotor(
            token,
            MotorUpdateRequest(id, namaMotor, brandId, tipe, tahun, harga, warna)
        )
    }

    suspend fun deleteMotor(id: Int): ResponseApi<Unit> {
        val token = getToken()
        return api.deleteMotor(token, id)
    }

    suspend fun tambahStok(motorId: Int): StokResponse {
        val token = getToken()
        return api.tambahStok(token, StokRequest(motorId))
    }

    suspend fun kurangiStok(motorId: Int): StokResponse {
        val token = getToken()
        return api.kurangiStok(token, StokRequest(motorId))
    }
}