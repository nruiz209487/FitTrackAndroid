package com.example.fittrack.type_converters

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

/**
 * Clase que yuso para convertir de latLng a string o al reves
 */
class LatLngConverter {

    @TypeConverter
    fun fromLatLng(latLng: LatLng?): String? {
        return latLng?.let { "${it.latitude},${it.longitude}" }
    }

    @TypeConverter
    fun toLatLng(value: String?): LatLng? {
        return value?.split(",")?.let {
            val lat = it[0].toDoubleOrNull()
            val lng = it[1].toDoubleOrNull()
            if (lat != null && lng != null) LatLng(lat, lng) else null
        }
    }
}