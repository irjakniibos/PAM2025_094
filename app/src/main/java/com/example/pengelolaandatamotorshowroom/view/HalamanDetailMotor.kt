package com.example.pengelolaandatamotorshowroom.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pengelolaandatamotorshowroom.R
import com.example.pengelolaandatamotorshowroom.ui.theme.*
import com.example.pengelolaandatamotorshowroom.viewmodel.DetailMotorViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HalamanDetailMotor(
    motorId: Int,
    onBack: () -> Unit,
    onEdit: (com.example.pengelolaandatamotorshowroom.modeldata.DataMotor) -> Unit,
    viewModel: DetailMotorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState

    LaunchedEffect(motorId) {
        viewModel.loadMotorDetail(motorId)
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
                    title = stringResource(R.string.detail_motor_title),
                    canNavigateBack = true,
                    onNavigateBack = onBack
                )
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
                                onClick = { viewModel.loadMotorDetail(motorId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary,
                                    contentColor = OnPrimary
                                )
                            ) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                    uiState.motor != null -> {
                        val motor = uiState.motor
                        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(R.dimen.padding_medium)),
                            colors = CardDefaults.cardColors(
                                containerColor = CardBackground
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(R.dimen.padding_large)),
                                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                            ) {
                                Text(
                                    text = motor.nama_motor,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Primary
                                )

                                Divider(color = CardOnBackground.copy(alpha = 0.3f))

                                DetailRow(stringResource(R.string.motor_brand), motor.nama_brand)
                                DetailRow(stringResource(R.string.motor_type), motor.tipe)
                                DetailRow(stringResource(R.string.motor_year), motor.tahun.toString())
                                DetailRow(stringResource(R.string.motor_price), formatRupiah.format(motor.harga))
                                DetailRow(stringResource(R.string.motor_color), motor.warna)
                                DetailRow(stringResource(R.string.motor_stock), motor.jumlah_stok.toString())

                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                                // Button Edit di dalam Card
                                Button(
                                    onClick = { onEdit(motor) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Primary,
                                        contentColor = OnPrimary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_action))
                                    )
                                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                                    Text(stringResource(R.string.btn_edit_motor))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = CardOnBackground.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = CardOnBackground
        )
    }
}