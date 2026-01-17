package com.example.pengelolaandatamotorshowroom.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.pengelolaandatamotorshowroom.modeldata.DataMotor
import com.example.pengelolaandatamotorshowroom.ui.theme.*
import com.example.pengelolaandatamotorshowroom.viewmodel.ListMotorViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HalamanListMotor(
    brandId: Int,
    brandName: String,
    onBack: () -> Unit,
    onAddMotor: () -> Unit,
    onDetailClick: (Int) -> Unit,
    viewModel: ListMotorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(brandId) {
        viewModel.loadMotors(brandId, brandName)
    }

    // Tampilkan toast ketika ada successMessage
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.backgroundone),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Content dengan overlay
        Scaffold(
            topBar = {
                ShowroomTopAppBar(
                    title = "Motor $brandName",
                    canNavigateBack = true,
                    onNavigateBack = onBack
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddMotor,
                    containerColor = Primary,
                    contentColor = OnPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Motor")
                }
            },
            containerColor = Background.copy(alpha = 0.85f) // Semi-transparent
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
                            Button(
                                onClick = { viewModel.loadMotors(brandId, brandName) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary,
                                    contentColor = OnPrimary
                                )
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                    uiState.motors.isEmpty() -> {
                        Text(
                            text = stringResource(R.string.motor_empty_hint),
                            color = OnBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                        ) {
                            items(uiState.motors) { motor ->
                                MotorCard(
                                    motor = motor,
                                    onDetailClick = { onDetailClick(motor.id) },
                                    onDeleteClick = { viewModel.showDeleteDialog(motor) },
                                    onAddStok = { viewModel.showStokDialog(motor, true) },
                                    onReduceStok = { viewModel.showStokDialog(motor, false) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text(stringResource(R.string.delete_motor_title), color = OnSurface) },
            text = {
                Text(
                    "Yakin hapus motor ${uiState.motorToDelete?.nama_motor}?",
                    color = OnSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteMotor() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error,
                        contentColor = OnError
                    )
                ) {
                    Text(stringResource(R.string.btn_delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteDialog() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Disabled
                    )
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }
            },
            containerColor = Surface
        )
    }

    // Stock Confirmation Dialog
    if (uiState.showStokDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideStokDialog() },
            title = {
                Text(
                    text = stringResource(
                        if (uiState.isAddStok) R.string.stock_add_title else R.string.stock_reduce_title
                    ),
                    color = OnSurface
                )
            },
            text = {
                Text(
                    stringResource(
                        if (uiState.isAddStok) R.string.stock_add_confirm else R.string.stock_reduce_confirm
                    ),
                    color = OnSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.updateStok() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isAddStok) Success else Error,
                        contentColor = if (uiState.isAddStok) OnSuccess else OnError
                    )
                ) {
                    Text(stringResource(R.string.btn_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideStokDialog() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Disabled
                    )
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }
            },
            containerColor = Surface
        )
    }
}

@Composable
fun MotorCard(
    motor: DataMotor,
    onDetailClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddStok: () -> Unit,
    onReduceStok: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            // Row 1: Nama | Tipe | Tahun | Warna
            Text(
                text = "${motor.nama_motor} | ${motor.tipe} | ${motor.tahun} | ${motor.warna}",
                style = MaterialTheme.typography.titleMedium,
                color = CardOnBackground
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            // Row 2: Harga
            Text(
                text = formatRupiah.format(motor.harga),
                style = MaterialTheme.typography.bodyLarge,
                color = Primary
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            // Row 3: Stock controls & Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Controls: [-] 5 [+]
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onReduceStok,
                        enabled = motor.jumlah_stok > 0,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (motor.jumlah_stok > 0) Error else Disabled,
                            contentColor = if (motor.jumlah_stok > 0) OnError else OnDisabled
                        ),
                        modifier = Modifier.size(dimensionResource(R.dimen.stock_button_size))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Kurangi Stok",
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_action))
                        )
                    }

                    Text(
                        text = motor.jumlah_stok.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = CardOnBackground,
                        modifier = Modifier.widthIn(min = dimensionResource(R.dimen.min_stock_width)),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = onAddStok,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Success,
                            contentColor = OnSuccess
                        ),
                        modifier = Modifier.size(dimensionResource(R.dimen.stock_button_size))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Stok",
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_action))
                        )
                    }
                }

                // Action Buttons: Detail & Delete
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
                ) {
                    IconButton(
                        onClick = onDetailClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary
                        ),
                        modifier = Modifier.size(dimensionResource(R.dimen.stock_button_size))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Detail",
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_action))
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Error,
                            contentColor = OnError
                        ),
                        modifier = Modifier.size(dimensionResource(R.dimen.stock_button_size))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_action))
                        )
                    }
                }
            }
        }
    }
}