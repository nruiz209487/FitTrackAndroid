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
    }

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra("note_id", -1)
        val noteTitle = intent.getStringExtra("note_title") ?: "Recordatorio"
        val noteContent = intent.getStringExtra("note_content") ?: "Tienes una nota pendiente"


        createNotificationChannel(context)

        if (!hasNotificationPermission(context)) {
            android.util.Log.w("NotificationReceiver", "No notification permission, cannot show notification")
            return
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(noteTitle)
            .setContentText(noteContent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(noteContent))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(noteId, notification)
            android.util.Log.d("NotificationReceiver", "Notification shown successfully for note $noteId")
        } catch (e: SecurityException) {
            android.util.Log.e("NotificationReceiver", "SecurityException showing notification: ${e.message}")
        } catch (e: Exception) {
            android.util.Log.e("NotificationReceiver", "Error showing notification: ${e.message}")
        }
    }

    private fun createNotificationChannel(context: Context) {

            val importance = NotificationManager.IMPORTANCE_HIGH // Cambiado a HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

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