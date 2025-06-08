package com.example.fittrack.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "target_location_table")
data class TargetLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val position: LatLng,
    val radiusMeters: Double = 200.0
)
