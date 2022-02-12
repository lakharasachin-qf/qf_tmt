package com.themarkettheory.user.firebase

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gotem.firebase.NotificationUtils
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Constants
import com.themarkettheory.user.helper.Prefs
import com.themarkettheory.user.ui.main.activity.FeedbackActivity
import com.themarkettheory.user.ui.main.activity.MainActivity
import com.themarkettheory.user.ui.main.activity.MyOrdersActivity
import com.themarkettheory.user.ui.main.activity.MyTableBookingsActivity
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = MyFirebaseMessagingService::class.java.simpleName

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val prefs = Prefs(applicationContext)
        prefs.setStringValue(Constants.fcm_token, p0)
        prefs.setToken(applicationContext, p0)
        Log.d("##Token", p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("##Token", "Notification Comes")

        Log.e(TAG, "From: " + remoteMessage.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
//            if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
            try {
                val map = remoteMessage.data
                if (map != null) {
                    handleDataMessage(map)
                } else {
                    handleNotification(remoteMessage.notification?.body, remoteMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }
//            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.notification!!.body!!)
//            handleNotification(remoteMessage.notification!!.body, remoteMessage)
            if (NotificationUtils.isAppIsInBackground(applicationContext)) {
                handleDataMessage(remoteMessage.data)
            }
        }
    }

    private fun handleNotification(message: String?, remoteMessage: RemoteMessage?) {
        if (!NotificationUtils.isAppIsInBackground(applicationContext)) {
            // app is in foreground, broadcast the push message
            if (message != "Resume Maintenance") {
                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                pushNotification.putExtra("message", message)
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                val msg = remoteMessage!!.notification?.body
                val key = remoteMessage!!.notification?.bodyLocalizationKey

                val resultIntent = Intent(applicationContext, MainActivity::class.java)
                /*val resultIntent = Intent(applicationContext, NotificationActivity::class.java)
                resultIntent.putExtra("message", message)*/

                NotificationUtils.showNotificationMessage(
                    resources.getString(R.string.app_name),
                    message,
                    System.currentTimeMillis(),
                    resultIntent,
                    "",
                    applicationContext
                )
                // play notification sound
                NotificationUtils.playNotificationSound(applicationContext)
            }
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }


    var ORDER_DELIVER = "3"
    var ORDER_ACCEPT = "5"
    var ORDER_REJECT = "6"
    var ORDER_CANCEL = "15"
    var BOOKING_ACCEPT = "8"
    var BOOKING_REJECT = "4"
    var BOOKING_CANCEL = "18"
    var FEEDBACK_REQUEST = "10"

    private fun handleDataMessage(map: Map<String, String>) {
        try {
            Log.e("RECEEVED", map.toString())
            val title = map["title"]
            var message: String?
            var imageUrl = map["image"]
            val type = map["type"]
            val notification_type = map["notification_type"]


            val sender_id = map["sender_id"]
            val sender_name = map["sender_name"]
            val sender_image = map["sender_image"]
            val receiver_id = map["receiver_id"]
            val message_type = map["chat_type"]
            val message_text = map["chat_text"]
            var serviceId = 0 //map["chat_text"]
            val timestamp = System.currentTimeMillis()
            val prefs = Prefs(applicationContext)
            message = map["body"]
            if (message.isNullOrEmpty())
                message = ""

            if (message_type == "image")
                imageUrl = message_text

            if (notification_type == "10")
                serviceId = map["service_id"]!!.toUInt().toInt()

            val pushNotification = Intent(Config.PUSH_NOTIFICATION)
            pushNotification.putExtra("message", message)
            pushNotification.putExtra("notification_type", notification_type)
            pushNotification.putExtra("serviceId", serviceId)
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)

            if (!prefs.getLoginModel().id.toString().isNullOrEmpty()) {

                val pushNotification = Intent("requests_count")
                //pushNotification.putExtra("message", message)
                pushNotification.putExtra("message", map["user_message"])
                pushNotification.putExtra("notification_type", notification_type)
                pushNotification.putExtra("serviceId", serviceId)

                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)

                var resultIntent = Intent(applicationContext, MainActivity::class.java)

                when (notification_type) {
                    ORDER_DELIVER -> {
                        resultIntent = Intent(applicationContext, MyOrdersActivity::class.java)
                    }
                    ORDER_ACCEPT -> {
                        resultIntent = Intent(applicationContext, MyOrdersActivity::class.java)
                    }
                    ORDER_REJECT -> {
                        resultIntent = Intent(applicationContext, MyOrdersActivity::class.java)
                    }
                    ORDER_CANCEL -> {
                        resultIntent = Intent(applicationContext, MyOrdersActivity::class.java)
                    }
                    BOOKING_ACCEPT -> {
                        resultIntent =
                            Intent(applicationContext, MyTableBookingsActivity::class.java)
                    }
                    BOOKING_REJECT -> {
                        resultIntent =
                            Intent(applicationContext, MyTableBookingsActivity::class.java)
                    }
                    BOOKING_CANCEL -> {
                        resultIntent =
                            Intent(applicationContext, MyTableBookingsActivity::class.java)
                    }
                    FEEDBACK_REQUEST -> {
                        resultIntent = Intent(applicationContext, FeedbackActivity::class.java)
                    }
                }
                Log.e("FDGDFgd", "Notification Type")
                /*val resultIntent = Intent(applicationContext, NotificationActivity::class.java)
                resultIntent.putExtra("message", message)
                resultIntent.putExtra("notificationFlag", true)*/

                val intent = Intent(Config.PUSH_NOTIFICATION)
                intent.setAction("OPEN_NEW_ACTIVITY")

                applicationContext.sendBroadcast(intent)

                showNotificationMessage(
                    title,
                    message,
                    timestamp,
                    resultIntent,
                    imageUrl,
                    applicationContext
                )
                // NotificationUtils.playNotificationSound(applicationContext)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showNotificationMessage(
        title: String?,
        message: String?,
        timeStamp: Long,
        intent: Intent,
        imageUrl: String?,
        context: Context
    ) {
        // Check for empty push message
//        if (TextUtils.isEmpty(message))
//            return

        // notification icon
        val icon: Int = R.drawable.ic_notification_icon

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        val resultPendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val mBuilder = NotificationCompat.Builder(
            context, Config.NOTIFICATION_CHANNEL_ID
        )

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val descriptionText = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(Config.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val alarmSound: Uri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.packageName + "/raw/notification"
        )

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl!!.length > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                val bitmap: Bitmap? = getBitmapFromURL(imageUrl)

                if (bitmap != null) {
                    showBigNotification(
                        bitmap,
                        mBuilder,
                        icon,
                        title,
                        message,
                        timeStamp,
                        resultPendingIntent,
                        alarmSound,
                        context
                    )
                } else {
                    showSmallNotification(
                        mBuilder,
                        icon,
                        title,
                        message,
                        timeStamp,
                        resultPendingIntent,
                        alarmSound,
                        context
                    )
                }
            }
        } else {
            showSmallNotification(
                mBuilder,
                icon,
                title,
                message,
                timeStamp,
                resultPendingIntent,
                alarmSound,
                context
            )
            playNotificationSound(context)
        }
    }

    private fun showSmallNotification(
        mBuilder: NotificationCompat.Builder,
        icon: Int,
        title: String?,
        message: String?,
        timeStamp: Long,
        resultPendingIntent: PendingIntent,
        alarmSound: Uri,
        context: Context
    ) {

        val inboxStyle = NotificationCompat.InboxStyle()

        inboxStyle.addLine(message)

        val notification: Notification
        notification =
            mBuilder.setSmallIcon(icon)
//                .setColor(ContextCompat.getColor(applicationContext,R.color.white))
                .setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setWhen(timeStamp)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, icon))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setChannelId(Config.NOTIFICATION_CHANNEL_ID)
                .build()

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Config.NOTIFICATION_ID, notification)
    }

    private fun showBigNotification(
        bitmap: Bitmap,
        mBuilder: NotificationCompat.Builder,
        icon: Int,
        title: String?,
        message: String?,
        timeStamp: Long,
        resultPendingIntent: PendingIntent,
        alarmSound: Uri,
        context: Context
    ) {
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bigPictureStyle.setSummaryText(
                Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT).toString()
            )
        } else {
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
        }
        bigPictureStyle.bigPicture(bitmap)
        val notification: Notification
        notification =
            mBuilder.setSmallIcon(icon)
                .setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(timeStamp)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, icon))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setChannelId(Config.NOTIFICATION_CHANNEL_ID)
                .build()

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification)
    }


    fun playNotificationSound(context: Context) {
        try {
            /*  Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");*/
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val r = RingtoneManager.getRingtone(context, uri)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Method checks if the app is in background or not
     */
    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo?.packageName == context.packageName) {
                isInBackground = false
            }
        }

        return isInBackground
    }

    // Clears notification tray messages
    fun clearNotifications(context: Context) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun getTimeMilliSec(timeStamp: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            val date = format.parse(timeStamp)
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    private fun getBitmapFromURL(strURL: String): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}