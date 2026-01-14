package com.example.pengelolaandatamotorshowroom.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pengelolaandatamotorshowroom.modeldata.DataBrand
import com.example.pengelolaandatamotorshowroom.repositori.RepositoryAuth
import com.example.pengelolaandatamotorshowroom.repositori.RepositoryBrand
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repositoryBrand: RepositoryBrand,
    private val repositoryAuth: RepositoryAuth
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        // Load brands saat ViewModel dibuat
        loadBrands()
    }

    fun loadBrands() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val brands = repositoryBrand.getAllBrands()
                uiState = uiState.copy(
                    brands = brands,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Terjadi kesalahan"
                )
            }
        }
    }

    fun showAddDialog() {
        uiState = uiState.copy(
            showDialog = true,
            isEditMode = false,
            dialogBrandName = "",
            selectedBrandId = null,
            dialogError = null
        )
    }

    fun showEditDialog(brand: DataBrand) {
        uiState = uiState.copy(
            showDialog = true,
            isEditMode = true,
            dialogBrandName = brand.nama_brand,
            selectedBrandId = brand.id,
            dialogError = null
        )
    }

    fun hideDialog() {
        uiState = uiState.copy(
            showDialog = false,
            dialogBrandName = "",
            dialogError = null,
            isDialogLoading = false
        )
    }

    fun updateDialogBrandName(name: String) {
        uiState = uiState.copy(dialogBrandName = name, dialogError = null)
    }

    fun saveBrand() {
        if (uiState.dialogBrandName.isBlank()) {
            uiState = uiState.copy(dialogError = "Nama brand tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isDialogLoading = true, dialogError = null)
            try {
                if (uiState.isEditMode && uiState.selectedBrandId != null) {
                    repositoryBrand.updateBrand(uiState.selectedBrandId!!, uiState.dialogBrandName)
                    uiState = uiState.copy(successMessage = "Brand berhasil diperbarui")
                } else {
                    repositoryBrand.createBrand(uiState.dialogBrandName)
                    uiState = uiState.copy(successMessage = "Brand berhasil ditambahkan")
                }
                hideDialog()
                loadBrands()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isDialogLoading = false,
                    dialogError = e.message ?: "Gagal menyimpan brand"
                )
            }
        }
    }

    fun showDeleteDialog(brand: DataBrand) {
        uiState = uiState.copy(showDeleteDialog = true, brandToDelete = brand)
    }

    fun hideDeleteDialog() {
        uiState = uiState.copy(showDeleteDialog = false, brandToDelete = null)
    }

    fun deleteBrand() {
        val brand = uiState.brandToDelete ?: return
        viewModelScope.launch {
            try {
                repositoryBrand.deleteBrand(brand.id)
                uiState = uiState.copy(successMessage = "Brand berhasil dihapus")
                hideDeleteDialog()
                loadBrands()
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message ?: "Gagal menghapus brand")
                hideDeleteDialog()
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repositoryAuth.logout()
                onSuccess()
            } catch (e: Exception) {
                // Ignore error and logout anyway
                onSuccess()
            }
        }
    }

    fun clearSuccessMessage() {
        uiState = uiState.copy(successMessage = null)
    }
}

data class DashboardUiState(
    val brands: List<DataBrand> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val dialogBrandName: String = "",
    val selectedBrandId: Int? = null,
    val isDialogLoading: Boolean = false,
    val dialogError: String? = null,
    val showDeleteDialog: Boolean = false,
    val brandToDelete: DataBrand? = null
)