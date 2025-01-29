package com.example.chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.example.chat.ui.theme.Purple
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class FirebaseMessageService : FirebaseMessagingService() {

    // Override onMessageReceived to handle incoming notifications
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FirebaseMessageService", "From: ${message.from}, Data: ${message.data}")

        // Get the title and body from the notification or data payload
        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]

        // Show the notification
        showNotification(title, body)
    }

    // Method to display the notification
    private fun showNotification(title: String?, message: String?) {
        // Avoid showing notifications if the message contains the current user's name
        FirebaseAuth.getInstance().currentUser?.let {
            if (title?.contains(it.displayName.toString()) == true || message?.contains(it.displayName.toString()) == true) return
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "messages",
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for chat notifications"
            notificationManager.createNotificationChannel(channel)
        }

        // Add custom large icon (you can replace with your own image)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)

        // Build notification with enhanced features
        val notification = NotificationCompat.Builder(this, "messages")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your own icon
            .setLargeIcon(largeIcon) // Set large icon for the notification
            .setColor(Purple.toArgb()) // Set custom notification color
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Add Big Text Style for longer messages
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority to high
            .setAutoCancel(true) // Dismiss the notification after tapping it
            .build()

        // Show the notification
        val notificationId = Random().nextInt(1000)
        notificationManager.notify(notificationId, notification)
    }
}