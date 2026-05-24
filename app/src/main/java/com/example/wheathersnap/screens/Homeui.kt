package com.example.wheathersnap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheathersnap.WheatherviewModel
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.example.wheathersnap.navndata.routes
import com.example.wheathersnap.permissions.PermissionCheck

@Composable
fun Homescreen(viewModel: WheatherviewModel , navController: NavHostController) {
    val city by viewModel.city.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    var showSuggestions by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    var cameraReady by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C300B),
                        Color(0xFF1F210B),
                        Color(0xFF141710),
                        Color.Black
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(500.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .blur(180.dp)
                .background(
                    Color(0xFFC1CC7D).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(300.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!cameraReady) {
                PermissionCheck(onPermissionsGranted = { cameraReady = true })
            }

            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFC0CC84),
                                    Color(0xFFB0E6D8)
                                )
                            )
                        )
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WeatherSnap",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFF2D3B1F)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Live weather reports with camera evidence",
                                fontSize = 12.sp,
                                color = Color(0xFF4A5C35),
                                maxLines = 1
                            )
                        }
                        Button(
                            onClick = {navController.navigate(routes.reporthistory)},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2D3B1F)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(text = "Reports", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1F0F))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = {
                                    viewModel.onCityChange(it)
                                    showSuggestions = true
                                    viewModel.searchCity(it)
                                },
                                label = { Text(text = "City", color = Color.Gray) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedBorderColor = Color.DarkGray,
                                    cursorColor = Color.White,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.clearSuggestions()
                                    showSuggestions = true
                                    viewModel.searchCity(city)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFC1CC7D)
                                ),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(text = "Search", color = Color.Black)
                            }
                        }


                        if (city.length in 1..2) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter more than 2 letters to start city suggestions.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        if (isLoading && showSuggestions) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .clip(RoundedCornerShape(50.dp)),
                                color = Color(0xFFC1CC7D),
                                trackColor = Color(0xFF2A2F1A)
                            )
                        }

                        if (showSuggestions && suggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            ElevatedCard(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF1E2410))
                                        .heightIn(max = 200.dp)
                                ) {
                                    items(suggestions) { result ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.onCityChange("${result.name}, ${result.country ?: ""}")
                                                    viewModel.fetchWeather(result)
                                                    viewModel.clearSuggestions()
                                                    showSuggestions = false
                                                }
                                                .padding(
                                                    horizontal = 16.dp,
                                                    vertical = 12.dp
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = result.name,
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "${result.admin1 ?: ""}, ${result.country ?: ""}",
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                        HorizontalDivider(
                                            color = Color(0xFF2A2F1A),
                                            thickness = 0.5.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF252B14))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text =  selectedCity.ifEmpty { "Search a city" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = weather?.current?.weather_code?.let {
                                        viewModel.getWeatherCondition(it)
                                    } ?: "--",
                                    fontSize = 13.sp,
                                    color = Color.LightGray
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF2D3B1F),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = weather?.current?.temperature_2m
                                        ?.toInt()
                                        ?.let { "$it°C" }
                                        ?: "--",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF1A1F0F))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Humidity",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text =
                                                weather?.current?.relative_humidity_2m
                                                    ?.let { "$it%" }
                                                    ?: "--",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4FC3F7)
                                        )
                                    }
                                }
                            }

                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF1A1F0F))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Wind",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = weather?.current?.wind_speed_10m
                                                ?.let { "$it m/s" }
                                                ?: "--",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4FC3F7)
                                        )
                                    }
                                }
                            }

                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF1A1F0F))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Pressure",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = weather?.current?.surface_pressure
                                                ?.let { "$it hPa" }
                                                ?: "--",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFB74D)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFF1A1F0F),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Report readiness",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            Text(
                                text =
                                    if (cameraReady == true) {"✓ Camera and Room DB enabled"}
                                    else {"⚠ Camera permission needed"},
                                fontSize = 13.sp,
                                color = if (cameraReady) Color(0xFFC1CC7D) else Color(0xFFFF7043),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))


                        Button(
                            onClick = { navController.navigate(routes.report) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC1CC7D)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Create Report",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}