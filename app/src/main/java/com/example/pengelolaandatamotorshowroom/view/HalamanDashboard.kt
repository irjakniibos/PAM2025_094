package com.example.pengelolaandatamotorshowroom.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pengelolaandatamotorshowroom.R
import com.example.pengelolaandatamotorshowroom.modeldata.DataBrand
import com.example.pengelolaandatamotorshowroom.ui.theme.*
import com.example.pengelolaandatamotorshowroom.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDashboard(
    onBrandClick: (Int, String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Tampilkan toast ketika ada successMessage
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.backgroundone),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                ShowroomTopAppBar(
                    title = stringResource(R.string.dashboard_title),
                    showLogout = true,
                    onLogout = { showLogoutDialog = true }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddDialog() },
                    containerColor = Primary,
                    contentColor = OnPrimary
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_button)
                    )
                }
            },
            containerColor = Background.copy(alpha = 0.85f)
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Primary
                        )
                    }

                    uiState.errorMessage != null -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color = Error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                            Button(onClick = { viewModel.loadBrands() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }

                    uiState.brands.isEmpty() -> {
                        Text(
                            text = stringResource(R.string.brand_empty),
                            color = OnBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                        ) {
                            items(uiState.brands) { brand ->
                                BrandCard(
                                    brand = brand,
                                    onClick = {
                                        onBrandClick(brand.id, brand.nama_brand)
                                    },
                                    onEdit = { viewModel.showEditDialog(brand) },
                                    onDelete = { viewModel.showDeleteDialog(brand) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /* ===== Add / Edit Dialog ===== */
    if (uiState.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDialog() },
            title = {
                Text(
                    text = stringResource(
                        if (uiState.isEditMode)
                            R.string.brand_edit_title
                        else
                            R.string.brand_add_title
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = uiState.dialogBrandName,
                    onValueChange = { viewModel.updateDialogBrandName(it) },
                    label = { Text(stringResource(R.string.brand_name)) },
                    isError = uiState.dialogError != null,
                    supportingText = {
                        uiState.dialogError?.let {
                            Text(it, color = Error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.saveBrand() }) {
                    Text(stringResource(R.string.btn_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDialog() }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    /* ===== Delete Dialog ===== */
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = {
                Text(stringResource(R.string.btn_delete))
            },
            text = {
                Text(stringResource(R.string.brand_delete_confirm))
            },
            confirmButton = {
                Button(onClick = { viewModel.deleteBrand() }) {
                    Text(stringResource(R.string.btn_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    /* ===== Logout Dialog ===== */
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.btn_logout)) },
            text = { Text(stringResource(R.string.logout_confirm)) },
            confirmButton = {
                Button(onClick = {
                    showLogoutDialog = false
                    Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                    viewModel.logout(onLogout)
                }) {
                    Text(stringResource(R.string.btn_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.btn_no))
                }
            }
        )
    }
}


@Composable
fun BrandCard(
    brand: DataBrand,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.brand_card_height_large))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.card_margin)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon MOTOR
            Icon(
                imageVector = Icons.Default.TwoWheeler,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(dimensionResource(R.dimen.brand_icon_size))
            )

            // Brand Name
            Text(
                text = brand.nama_brand,
                style = MaterialTheme.typography.titleMedium,
                color = CardOnBackground,
                textAlign = TextAlign.Center
            )

            // Text hint
            Text(
                text = stringResource(R.string.brand_click_hint),
                style = MaterialTheme.typography.bodySmall,
                color = CardOnBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(dimensionResource(R.dimen.small_button_size))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_action))
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(dimensionResource(R.dimen.small_button_size))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Error,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_action))
                    )
                }
            }
        }
    }
}