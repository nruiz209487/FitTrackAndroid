package com.example.fittrack.type_converters

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Funcion que uso para las coversiones de fechas en el codigo
 */
fun formatGlobalTimestamp(input: String): String {
    return try {
        when {
            input.startsWith("NOTIFICATION:") -> {
                val raw = input.removePrefix("NOTIFICATION:")
                val dateTime = LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                "Recordatorio: ${dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}"
            }

            input.contains("/") -> {
                val parts = input.split("/")
                if (parts.size == 3) {
                    val date = LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } else input
            }

            input.contains("-") -> {
                val date = LocalDate.parse(input, DateTimeFormatter.ISO_DATE)
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }

            else -> input
        }
    } catch (e: Exception) {
        input
    }
}
