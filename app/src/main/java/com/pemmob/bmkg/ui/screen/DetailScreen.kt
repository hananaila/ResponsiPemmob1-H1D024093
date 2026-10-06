package com.pemmob.bmkg.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.South
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.bmkg.data.model.Gempa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    gempa: Gempa,
    onBackClick: () -> Unit
) {
    val magValue = gempa.magnitude.toDoubleOrNull() ?: 0.0
    val magColor = if (magValue >= 6.0) Color(0xFFD32F2F) else Color(0xFFE65100)
    val (namaKota, jarakArah) = parseWilayahBMKG(gempa.wilayah)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Gempa Bumi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
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
                .verticalScroll(rememberScrollState())
        ) {
            // === HEADER ELEGAN ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(top = 10.dp, bottom = 22.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Lingkaran Magnitudo Besar
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .border(4.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .background(magColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = gempa.magnitude,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 30.sp
                        )
                        Text(
                            text = "Magnitudo",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 10.sp
                        )
                    }
                }

                // Nama Kota (Tebal)
                Text(
                    text = namaKota,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Subtitle Jarak
                if (jarakArah.isNotEmpty()) {
                    Text(
                        text = jarakArah,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // BADGE TSUNAMI
                val isSafe = gempa.potensi.contains("tidak", ignoreCase = true)
                val badgeIcon = if (isSafe) Icons.Default.CheckCircle else Icons.Default.Warning
                val badgeColor = if (isSafe) Color(0xFF2E7D32) else Color(0xFFD32F2F)

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badgeColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = badgeIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = gempa.potensi.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // === BAR SKALA KEKUATAN GEMPA ===
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tingkat Kekuatan Gempa",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when {
                                magValue >= 7.0 -> "Sangat Kuat"
                                magValue >= 6.0 -> "Kuat"
                                magValue >= 5.0 -> "Sedang"
                                else -> "Ringan"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = magColor
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF4CAF50)))
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFFB300)))
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFB8C00)))
                        Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFD32F2F)))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // === GRID 2x2 PARAMETER DATA TEKNIS ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Baris 1: Tanggal & Waktu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailInfoBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CalendarToday,
                        label = "Tanggal",
                        value = gempa.tanggal
                    )
                    DetailInfoBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Schedule,
                        label = "Waktu Gempa",
                        value = gempa.jam
                    )
                }

                // Baris 2: Kedalaman & Koordinat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailInfoBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.South,
                        label = "Kedalaman",
                        value = gempa.kedalaman
                    )
                    DetailInfoBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Place,
                        label = "Koordinat",
                        value = formatKoordinatBMKG(gempa.coordinates)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // === CARD ARAHAN MITIGASI BMKG ===
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Baris Atas: Ikon + Judul
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Arahan BMKG",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Baris Bawah: Paragraf membentang penuh
                    Text(
                        text = "Hati-hati terhadap gempabumi susulan yang mungkin terjadi. Tetap tenang dan hindari bangunan yang retak atau rusak.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DetailInfoBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp), // Dirapatkan vertikalnya (12.dp -> 10.dp)
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp) // Jarak antar elemen dalam box sangat dirapatkan (4.dp -> 1.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
