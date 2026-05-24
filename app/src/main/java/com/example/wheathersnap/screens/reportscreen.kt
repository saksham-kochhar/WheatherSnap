package com.example.wheathersnap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wheathersnap.WheatherviewModel
import com.example.wheathersnap.navndata.routes
private val BgPage      = Color(0xFF0D1008)
private val BgCard      = Color(0xFF181D0E)
private val BgChip      = Color(0xFF222918)
private val AccentLime  = Color(0xFFC5D37A)
private val AccentDark  = Color(0xFF2D3122)
private val TextPrimary = Color(0xFFF0F4E0)
private val TextSub     = Color(0xFF8A9470)

@Composable
fun reportscreen(
    navcontroller: NavHostController,
    viewModel: WheatherviewModel
) {
    var notes by remember { mutableStateOf("") }
    val weather by viewModel.weather.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val capturedUri by viewModel.lastCapturedUri.collectAsState()
    val originalSizeKb by viewModel.originalKb.collectAsState()
    val compressedSizeKb by viewModel.compressedKb.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AccentLime),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Create Report",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentDark
                    )
                    Text(
                        text = "Capture · Compress · Annotate",
                        color = Color(0xFF4A5230),
                        fontSize = 13.sp
                    )
                }
                Button(
                    onClick = { navcontroller.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentDark),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Back", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedCity.ifEmpty { "Search a city" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = weather?.current?.weather_code
                                ?.let { viewModel.getWeatherCondition(it) } ?: "--",
                            color = TextSub,
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(BgChip, RoundedCornerShape(14.dp))
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = weather?.current?.temperature_2m
                                ?.toInt()?.let { "$it°C" } ?: "--",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentLime
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WeatherInfoCard(
                        title = "Humidity",
                        value = weather?.current?.relative_humidity_2m
                            ?.let { "$it%" } ?: "--",
                        valueColor = Color(0xFF4CD1A3),
                        modifier = Modifier.weight(1f)
                    )
                    WeatherInfoCard(
                        title = "Wind",
                        value = weather?.current?.wind_speed_10m
                            ?.let { "$it m/s" } ?: "--",
                        valueColor = Color(0xFF4A90FF),
                        modifier = Modifier.weight(1f)
                    )
                    WeatherInfoCard(
                        title = "Pressure",
                        value = weather?.current?.surface_pressure
                            ?.let { "$it hPa" } ?: "--",
                        valueColor = Color(0xFFFFA640),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Photo Evidence",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BgChip),
                    contentAlignment = Alignment.Center
                ) {
                    if (capturedUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(capturedUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Captured photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = TextSub,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No photo captured yet",
                                color = TextSub,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                if (capturedUri != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgChip, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Original", fontSize = 11.sp, color = TextSub)
                            Text(
                                text = originalSizeKb?.let { "~$it KB" } ?: "--",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF7043)
                            )
                        }
                        Text(
                            text = "→",
                            fontSize = 18.sp,
                            color = TextSub,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Compressed", fontSize = 11.sp, color = TextSub)
                            Text(
                                text = compressedSizeKb?.let { "$it KB" } ?: "--",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentLime
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Saved", fontSize = 11.sp, color = TextSub)
                            Text(
                                text = if (originalSizeKb != null && compressedSizeKb != null)
                                    "${((1f - compressedSizeKb!!.toFloat() / originalSizeKb!!) * 100).toInt()}%"
                                else "--",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CD1A3)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navcontroller.navigate(routes.camera) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = AccentDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (capturedUri == null) "Capture Photo" else "Retake Photo",
                        color = AccentDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Field Notes",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder = {
                        Text("Add observation notes...", color = TextSub)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentLime,
                        unfocusedBorderColor = BgChip,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentLime,
                        focusedContainerColor = BgChip,
                        unfocusedContainerColor = BgChip
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = {
                val uri = capturedUri?.toString() ?: return@Button
                viewModel.saveReport(
                    city = selectedCity.ifEmpty { "Unknown" },
                    weatherCondition = weather?.current?.weather_code
                        ?.let { viewModel.getWeatherCondition(it) } ?: "--",
                    temperature = weather?.current?.temperature_2m
                        ?.toInt()?.let { "$it°C" } ?: "--",
                    humidity = weather?.current?.relative_humidity_2m
                        ?.let { "$it%" } ?: "--",
                    wind = weather?.current?.wind_speed_10m
                        ?.let { "$it m/s" } ?: "--",
                    pressure = weather?.current?.surface_pressure
                        ?.let { "$it hPa" } ?: "--",
                    imageUri = uri,
                    originalKb = originalSizeKb ?: 0L,
                    compressedKb = compressedSizeKb ?: 0L,
                    notes = notes
                )
                navcontroller.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (capturedUri != null) AccentLime else BgChip
            ),
            enabled = capturedUri != null
        ) {
            Text(
                text = "Save Report",
                color = if (capturedUri != null) AccentDark else TextSub,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun WeatherInfoCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222918)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = Color(0xFF8A9470), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}