package com.example.prog7313_sem1_part2

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.content.Context
import java.util.*

//(Ariefzuhri, 2021)
class MyMessagingService : FirebaseMessagingService() {


     //This class is a service class that runs in the background, detecting every time a new notification is received
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Get messages from getNotification()
        val title = remoteMessage.notification?.title?: "Shared Wallet"
        val message = remoteMessage.notification?.body?: "You were added to a new wallet. Start budgeting together!"

        showNotification(this, title, message)
    }

    //going to call this function to the Wallet fragment to send notification on click (when saving wallet)
    companion object {
        fun showNotification(context: Context, title: String, message: String) {

            // added intent filter for intent action in Android manifest
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            @SuppressLint("UnspecifiedImmutableFlag") val pendingIntent = PendingIntent
                .getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val channelId = "wallet_share"

            val builder = NotificationCompat.Builder(context, channelId)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.blue_and_yellow_abstract_investment_logo__8_)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setSound(defaultSoundUri)
                .setAutoCancel(true)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    "Wallet Invites",
                    NotificationManager.IMPORTANCE_HIGH
                )
                builder.setChannelId(channelId)
                notificationManager.createNotificationChannel(notificationChannel)
            }

            val notification = builder.build()
            // Keep multiple notifications
            val notificationId = Random().nextInt()
            notificationManager.notify(notificationId, notification)
        }

    }

    //gets the new token when user registers or logs in
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }

}

/* References

Ariefzuhri. 2021. FCMSpecificNotifications. (version 8) [Source Code]. Available at: <https://github.com/ariefzuhri/FCMSpecificNotifications> [Accessed 2 June 2026]
Firebase. 2026. REST Resource: projects.messages. (version 8) [Source Code]. Available at: <https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages> [Accessed 2 June 2026]
Firebase. 2026b. Your server environment and FCM. (version 8) [Source Code]. Available at: <https://firebase.google.com/docs/cloud-messaging/server-environment> [Accessed 2 June 2026]
Lackner, F. 2024. How to implement Firebase Push Notification on Android (FCM + Backend). (version 8) [Source Code]. Available at: <https://youtu.be/q6TL2RyysV4?si=5w3LO17wcgH2aa3N> [Accessed 2 June 2026]
Mobile App Development. 2024. How to implement FCM in Android Kotlin | Firebase Cloud Messaging | Mobile App Development. (version 8) [Source Code]. Available at: <https://youtu.be/Ywn5FwTQwOU?si=T0Hm4s4yfwg9JDKi> [Accessed 2 June 2026]

*/