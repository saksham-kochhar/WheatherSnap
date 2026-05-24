package com.example.wheathersnap.navndata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val city: String,
    val weatherCondition: String,
    val temperature: String,
    val humidity: String,
    val wind: String,
    val pressure: String,
    val imageUri: String,
    val originalKb: Long,
    val compressedKb: Long,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)