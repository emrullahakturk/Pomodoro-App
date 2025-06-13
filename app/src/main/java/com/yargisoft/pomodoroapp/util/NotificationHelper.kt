package com.yargisoft.pomodoroapp.util
// app/src/main/java/com/seninuygulamaninadi/util/NotificationHelper.kt

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yargisoft.pomodoroapp.MainActivity
import com.yargisoft.pomodoroapp.R

object NotificationHelper {

    private const val CHANNEL_ID = "pomodoro_notification_channel"
    private const val CHANNEL_NAME = "Pomodoro Bildirimleri"
    private const val CHANNEL_DESCRIPTION = "Pomodoro zamanlayıcısı bildirimleri"
    private const val NOTIFICATION_ID = 1001

    // Bildirim kanalını oluşturur. Uygulama başlangıcında çağrılmalı.
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH // Yüksek önem seviyesi, ses ve titreşim için
        ).apply {
            description = CHANNEL_DESCRIPTION
            enableVibration(true)
            // enableLights(true) // İsteğe bağlı
            // lightColor = Color.RED // İsteğe bağlı
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Bildirim gönderir.
    fun showNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE // Android 12+ için gerekli
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Küçük ikon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Bildirime tıklayınca intent'i çalıştır
            .setAutoCancel(true) // Tıklandığında bildirimi otomatik kapat

        with(NotificationManagerCompat.from(context)) {
            // İzin kontrolünü yap
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !checkNotificationPermission(context)
            ) {
                // Android 13+ ve izin yoksa bildirim gönderme
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    // Android 13+ için bildirim izni kontrolü (manuel olarak yapılması gerekir)
    // Bu kontrol MainActivity'de veya ilk kez bildirim gönderildiğinde yapılmalı.
    private fun checkNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
        return true // API 33 öncesi otomatik izin verilir
    }
}