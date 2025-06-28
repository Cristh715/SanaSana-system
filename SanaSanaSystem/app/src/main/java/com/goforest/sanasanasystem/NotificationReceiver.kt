package com.goforest.sanasanasystem

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        private const val CHANNEL_ID = "cita_medica_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "NotificationReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "NotificationReceiver.onReceive() llamado")
        
        try {
            val title = intent.getStringExtra("title") ?: "Recordatorio de Cita Médica"
            val message = intent.getStringExtra("message") ?: "Tu cita médica empezará dentro de 1 hora. ¡Prepárate para llegar a tiempo!"
            
            Log.d(TAG, "Título: $title")
            Log.d(TAG, "Mensaje: $message")
            
            createNotificationChannel(context)
            Log.d(TAG, "Canal de notificación creado/verificado")
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_hospital)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            Log.d(TAG, "Notificación construida correctamente")

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
            
            Log.d(TAG, "Notificación enviada al sistema con ID: $NOTIFICATION_ID")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear/enviar notificación: ${e.message}", e)
        }
    }

    private fun createNotificationChannel(context: Context) {
        Log.d(TAG, "Creando canal de notificación...")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Citas Médicas"
            val descriptionText = "Canal para notificaciones de citas médicas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            
            Log.d(TAG, "Canal de notificación creado: $CHANNEL_ID")
        }
    }
} 