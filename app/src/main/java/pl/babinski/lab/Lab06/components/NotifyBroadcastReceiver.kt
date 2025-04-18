package pl.babinski.lab.Lab06.components

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import pl.babinski.lab.Lab06.channelID
import pl.babinski.lab.Lab06.messageExtra
import pl.babinski.lab.Lab06.notificationID
import pl.babinski.lab.Lab06.titleExtra
import pl.babinski.lab.R

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra(titleExtra) ?: "Brak tytułu"
        val message = intent?.getStringExtra(messageExtra) ?: "Brak treści"

        Log.d("Alarm", "Notification received: $title - $message")

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.mipmap.ic_launcher) // <- poprawna, działa jako small icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}
