package com.android.ecommerce.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android.ecommerce.activities.LoggingRegisterActivity
import com.android.ecommerce.activities.ShoppingActivity
import com.android.ecommerce.util.Constants.CHANNEL_ID
import com.android.ecommerce.util.Constants.SHARED_PREF_CHECKING_FIRST_OPEN
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPreferences : SharedPreferences

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "Notification", it.body ?: "You have a new message!")
        }
    }

    private fun sendNotification(title : String,message : String) {
        val channelId = CHANNEL_ID
        val notificationId = System.currentTimeMillis().toInt()

        // Use injected SharedPreferences (same as in IntroductionViewModel)
        val isLoggedIn = sharedPreferences.getBoolean(SHARED_PREF_CHECKING_FIRST_OPEN, false)


        val intent = if(isLoggedIn){
            Intent(this,ShoppingActivity::class.java)
        } else {
            Intent(this,LoggingRegisterActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}