package com.example.fittrack.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fittrack.MainActivity
import com.example.fittrack.R
import com.example.fittrack.ui.helpers.NotificationCreator

/**
 * Servicio que se encarga de mostrar notificaciones en el sistema
 */
class NotificationService : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationService"
    }

    /**
     * Se encarga de extraer los datos y mostrar la notificación correspondiente.
     */
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra("note_id", -1)
        val noteTitle = intent.getStringExtra("note_title")
        val noteContent = intent.getStringExtra("note_content")
        val notificationId = intent.getIntExtra("notification_id", noteId)

        val notificationTitle = intent.getStringExtra("notification_title") ?: noteTitle ?: "Recordatorio"
        val notificationContent = intent.getStringExtra("notification_content") ?: noteContent ?: "Tienes una nota pendiente"

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

        // llama al NotificationCreator para tema de permisos
        if (!NotificationCreator.hasNotificationPermission(context)) {
            android.util.Log.w(TAG, "No notification permission, cannot show notification")
            return
        }

        showNotification(context, notificationId, notificationTitle, notificationContent, extraData)
    }

    /**
     * Muestra la notificación con los datos proporcionados.
     */
    private fun showNotification(
        context: Context,
        id: Int,
        title: String,
        content: String,
        extraData: Map<String, String>
    ) {
        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

        val notification = NotificationCompat.Builder(context, NotificationCreator.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)//iamgen
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
}