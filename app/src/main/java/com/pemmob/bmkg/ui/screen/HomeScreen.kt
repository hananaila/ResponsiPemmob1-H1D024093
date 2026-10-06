package com.pemmob.bmkg.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pemmob.bmkg.R
import com.pemmob.bmkg.data.model.Gempa
import com.pemmob.bmkg.ui.viewmodel.GempaUiState
import com.pemmob.bmkg.ui.viewmodel.GempaViewModel

// Parser Regex Akurat sesuai pola data BMKG: "[Jarak] km [Arah] [WILAYAH]"
fun parseWilayahBMKG(rawWilayah: String): Pair<String, String> {
    val regex = Regex("""^(\d+\s*km\s+[A-Za-z]+)\s+(.+)$""")
    val match = regex.find(rawWilayah.trim())
    
    return if (match != null) {
        val jarakArah = match.groupValues[1]
        val wilayahRaw = match.groupValues[2]
        
        val namaKota = wilayahRaw.replace("-", ", ")
            .split(" ")
            .joinToString(" ") { kata ->
                kata.lowercase().replaceFirstChar { it.uppercase() }
            }
        Pair(namaKota, jarakArah)
    } else {
        Pair(rawWilayah, "")
    }
}

fun formatKoordinatBMKG(rawCoordinates: String): String {
    val parts = rawCoordinates.split(",")
    if (parts.size == 2) {
        val lat = parts[0].trim().toDoubleOrNull()
        val lon = parts[1].trim().toDoubleOrNull()
        if (lat != null && lon != null) {
            val latStr = "${kotlin.math.abs(lat)} " + if (lat >= 0) "LU" else "LS"
            val lonStr = "${kotlin.math.abs(lon)} " + if (lon >= 0) "BT" else "BB"
            return "$latStr • $lonStr"
        }
    }
    return rawCoordinates
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GempaViewModel = viewModel(),
    onNavigateToDetail: (Gempa) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo_bmkg),
                                contentDescription = "Logo BMKG",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp)
                            )
                        }

                        Text(
                            text = "Monitoring Gempa BMKG",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // HEADER HIJAU
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(bottom = 16.dp)
            ) {
                // PANTAU GEMPA 1 BARIS PAS
                Text(
                    text = "Pantau Gempa, Tetap Waspada.",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // SEARCH BAR SIMETRIS PRESISI
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Cari Wilayah Gempa...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        // Kuncian .height(50.dp) dihapus agar padding internal Material 3 tidak membuat teks terpotong
                )
            }

            // LIST GEMPA
            when (val state = uiState) {
                is GempaUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is GempaUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Gagal memuat data", style = MaterialTheme.typography.titleMedium)
                            Text(text = state.message, color = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.fetchGempa() }) { Text("Coba Lagi") }
                        }
                    }
                }
                is GempaUiState.Success -> {
                    val filteredList = state.data.filter { it.wilayah.contains(searchQuery, ignoreCase = true) }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredList) { gempa ->
                            GempaItemCard(gempa = gempa, onClick = { onNavigateToDetail(gempa) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GempaItemCard(
    gempa: Gempa,
    onClick: () -> Unit
) {
    val magValue = gempa.magnitude.toDoubleOrNull() ?: 0.0
    val magColor = if (magValue >= 6.0) Color(0xFFD32F2F) else Color(0xFFE65100)
    val (namaKota, jarakArah) = parseWilayahBMKG(gempa.wilayah)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp), // Padding vertikal dipangkas dari 12.dp ke 10.dp agar ramping
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lingkaran Magnitudo
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(magColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gempa.magnitude,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Kolom Tengah: Kota, Jarak, Kedalaman (Merapatkan jarak baris)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp) // Jarak di-nol-kan agar menyatu harmonis
            ) {
                Text(
                    text = namaKota,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 16.sp, // LineHeight diatur agar teks 2 baris tidak saling menjauh
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (jarakArah.isNotEmpty()) {
                    Text(
                        text = jarakArah,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Kedalaman: ${gempa.kedalaman}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Kolom Kanan: Tanggal & Badge AMAN
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${gempa.tanggal} • ${gempa.jam.take(5)}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = if (gempa.potensi.contains("tidak", ignoreCase = true)) "AMAN" else "WASPADA",
                        color = if (gempa.potensi.contains("tidak", ignoreCase = true)) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
