package com.example.fittrack.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.fittrack.MainActivity
import com.example.fittrack.R

class NotificationService : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "notes_notifications"
        private const val CHANNEL_NAME = "Recordatorios de Notas"
        private const val CHANNEL_DESCRIPTION = "Notificaciones para recordatorios de notas"
        private const val TAG = "NotificationService"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Obtener datos del intent (compatibilidad con el formato anterior)
        val noteId = intent.getIntExtra("note_id", -1)
        val noteTitle = intent.getStringExtra("note_title")
        val noteContent = intent.getStringExtra("note_content")

        // Obtener datos del nuevo formato
        val notificationId = intent.getIntExtra("notification_id", noteId)
        val notificationTitle = intent.getStringExtra("notification_title") ?: noteTitle ?: "Recordatorio"
        val notificationContent = intent.getStringExtra("notification_content") ?: noteContent ?: "Tienes una nota pendiente"

        // Obtener datos extra
        val extraData = mutableMapOf<String, String>()
        intent.extras?.keySet()?.forEach { key ->
            if (key.startsWith("extra_")) {
                val value = intent.getStringExtra(key)
                if (value != null) {
                    extraData[key.removePrefix("extra_")] = value
                }
            }
        }

        android.util.Log.d(TAG, "Received notification request for ID: $notificationId")

        createNotificationChannel(context)

        if (!hasNotificationPermission(context)) {
            android.util.Log.w(TAG, "No notification permission, cannot show notification")
            return
        }

        showNotification(context, notificationId, notificationTitle, notificationContent, extraData)
    }

    private fun showNotification(
        context: Context,
        id: Int,
        title: String,
        content: String,
        extraData: Map<String, String>
    ) {
        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Agregar datos extra al intent de la actividad
            extraData.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(id, notification)
            android.util.Log.d(TAG, "Notification shown successfully for ID $id")
        } catch (e: SecurityException) {
            android.util.Log.e(TAG, "SecurityException showing notification: ${e.message}")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error showing notification: ${e.message}")
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
}