package com.example.wheathersnap.Roomdb

import androidx.room.*
import com.example.wheathersnap.navndata.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Delete
    suspend fun deleteReport(report: ReportEntity)
}