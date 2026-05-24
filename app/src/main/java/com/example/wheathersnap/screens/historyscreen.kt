package com.example.wheathersnap.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wheathersnap.WheatherviewModel
import com.example.wheathersnap.navndata.ReportEntity
import java.text.SimpleDateFormat
import java.util.*
private val BgPage     = Color(0xFF0D1008)
private val BgCard     = Color(0xFF181D0E)
private val BgChip     = Color(0xFF222918)
private val AccentLime = Color(0xFFC5D37A)
private val AccentDark = Color(0xFF2D3122)
private val TextPri    = Color(0xFFF0F4E0)
private val TextSub    = Color(0xFF8A9470)

@Composable
fun SavedReportsScreen(
    navController: NavHostController,
    viewModel: WheatherviewModel
) {
    val reports by viewModel.allReports.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {

        Card(
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = AccentLime),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saved Reports",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentDark
                    )
                    Text(
                        text = "${reports.size} report${if (reports.size != 1) "s" else ""} stored locally",
                        fontSize = 13.sp,
                        color = Color(0xFF4A5230)
                    )
                }
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentDark),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Back", color = Color.White)
                }
            }
        }

        if (reports.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📋", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No reports yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPri
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Create a report from the home screen",
                        fontSize = 13.sp,
                        color = TextSub
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(reports, key = { it.id }) { report ->
                    ReportCard(
                        report = report,
                        onDelete = { viewModel.deleteReport(report) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportCard(
    report: ReportEntity,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val dateStr = remember(report.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(report.timestamp))
    }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = BgCard,
            title = { Text("Delete Report", color = TextPri) },
            text = { Text("This report will be permanently deleted.", color = TextSub) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = Color(0xFFFF5252))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSub)
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(BgChip)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Uri.parse(report.imageUri))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Report photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = report.city,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPri,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = report.weatherCondition,
                            fontSize = 13.sp,
                            color = TextSub
                        )
                        Text(
                            text = dateStr,
                            fontSize = 11.sp,
                            color = TextSub
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(AccentDark, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = report.temperature,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentLime
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgChip, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Original", fontSize = 11.sp, color = TextSub)
                        Text(
                            text = "${report.originalKb} KB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF7043)
                        )
                    }
                    Text(
                        text = "→",
                        fontSize = 16.sp,
                        color = TextSub,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Column {
                        Text(text = "Compressed", fontSize = 11.sp, color = TextSub)
                        Text(
                            text = "${report.compressedKb} KB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentLime
                        )
                    }
                    Column {
                        Text(text = "Saved", fontSize = 11.sp, color = TextSub)
                        Text(
                            text = if (report.originalKb > 0)
                                "${((1f - report.compressedKb.toFloat() / report.originalKb) * 100).toInt()}%"
                            else "--",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CD1A3)
                        )
                    }
                }


                if (report.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgChip, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = report.notes,
                            fontSize = 13.sp,
                            color = TextPri,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFF5252).copy(alpha = 0.4f))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete Report", fontSize = 13.sp)
                }
            }
        }
    }
}