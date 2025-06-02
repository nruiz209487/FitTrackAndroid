package com.example.fittrack.ui.ui_elements

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.fittrack.R
import com.example.fittrack.service.NotificationService
import java.time.LocalDateTime
import java.time.ZoneId

object NotificationCreator {

    private const val TAG = "NotificationCreator"
    private const val CHANNEL_ID = "fittrack_notes_channel"
    private const val CHANNEL_NAME = "Recordatorios de Notas"

    /**
     * Verifica si la aplicación tiene permisos de notificación
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Programa una notificación para una fecha y hora específica
     */
    fun scheduleNotification(
        context: Context,
        notificationId: Int,
        title: String,
        content: String,
        dateTime: LocalDateTime,
        extraData: Map<String, String> = emptyMap(),
        @DrawableRes iconRes: Int = R.drawable.ic_launcher_foreground
    ): Boolean {
        return try {
            if (!hasNotificationPermission(context)) {
                Log.w(TAG, "No notification permission available")
                return false
            }

            createNotificationChannel(context)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = createNotificationIntent(context, notificationId, title, content, extraData, iconRes)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerTime = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // Verificar que la fecha no sea en el pasado
            if (triggerTime <= System.currentTimeMillis()) {
                Log.w(TAG, "Cannot schedule notification for past time")
                return false
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            Log.d(TAG, "Notification scheduled successfully for ID: $notificationId at $dateTime")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling notification: ${e.message}", e)
            false
        }
    }

    /**
     * Cancela una notificación programada
     */
    fun cancelNotification(context: Context, notificationId: Int): Boolean {
        return try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationService::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            // También cancela la notificación si ya está siendo mostrada
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(notificationId)

            Log.d(TAG, "Notification cancelled for ID: $notificationId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling notification: ${e.message}", e)
            false
        }
    }



    /**
     * Crea el Intent para la notificación con datos extra
     */
    private fun createNotificationIntent(
        context: Context,
        notificationId: Int,
        title: String,
        content: String,
        extraData: Map<String, String>,
        @DrawableRes iconRes: Int
    ): Intent {
        return Intent(context, NotificationService::class.java).apply {
            putExtra("notification_id", notificationId)
            putExtra("notification_title", title)
            putExtra("notification_content", content)
            putExtra("notification_icon", iconRes)

            // Agregar datos extra
            extraData.forEach { (key, value) ->
                putExtra("extra_$key", value)
            }
        }
    }

    /**
     * Crea un canal de notificación optimizado
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios de notas y eventos"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}