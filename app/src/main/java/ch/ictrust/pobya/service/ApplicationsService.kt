package ch.ictrust.pobya.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ch.ictrust.pobya.R
import ch.ictrust.pobya.utillies.ApplicationPermissionHelper
import ch.ictrust.pobya.utillies.Utilities
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.repository.ApplicationRepository
import kotlinx.coroutines.*
import java.util.Calendar


class ApplicationsService : Service() {
    private var receiver: BroadcastReceiver? = null
    private var TAG: String = this.javaClass.name
    private var NOTIFICATION_CHANNEL_ID = "PObY-A install"
    private var NOTIFICATION_CHANNEL_ID_service = "PObY-A service"

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationBuilderService: NotificationCompat.Builder


    override fun onCreate() {
        super.onCreate()
        Utilities.populateScope.launch {
            withContext(Dispatchers.IO) {
                val applicationPermissionHelper = ApplicationPermissionHelper(applicationContext, true)
                applicationPermissionHelper.getListApps(true)
                applicationPermissionHelper.getAllperms()
            }
        }
        Log.d(TAG, "The service has been created.")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        notificationBuilderService =
            NotificationCompat.Builder(baseContext, NOTIFICATION_CHANNEL_ID_service)

        val notificationBuilder = NotificationCompat.Builder(
            applicationContext, NOTIFICATION_CHANNEL_ID
        )

        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID_service,
                "PObY-A", NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Monitoring", NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        val appRepository = ApplicationRepository.getInstance(application)

        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Privacy Live Monitoring")
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(1, notification)

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }

        intentFilter.priority = 999

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "PObY-A",
                    NotificationManager.IMPORTANCE_HIGH
                )

                notificationChannel.description = "Applications actions"
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)

                var notificationBuilderAction =
                    NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

                val action = intent.action



                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                if (action == Intent.ACTION_PACKAGE_ADDED) {
                    val app = intent.data?.schemeSpecificPart?.let {
                        ApplicationPermissionHelper(applicationContext, true).getAppByPackageName(it)
                    }
                    Utilities.populateScope.launch {
                        withContext(Dispatchers.IO) {
                            app?.let { appRepository.insert(it) }
                        }
                    }

                    //app?.let { AppsViewModel(application).addApp(it) }

                    notificationBuilderAction.setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setTicker("PObY-A")
                        .setAutoCancel(true)
                        .setContentTitle("New application installed")
                        .setContentText("" + app?.name)
                        .setContentInfo("Info")
                    notificationManager.notify(2, notificationBuilderAction.build())


                } else if (action == Intent.ACTION_PACKAGE_REMOVED) {

                    var app: InstalledApplication

                    Utilities.dbScope.launch {
                        val applicationRepository = ApplicationRepository.getInstance(application)
                        val currentDate = Calendar.getInstance().time
                        app = applicationRepository.getAppByPackageName(
                            intent.data?.schemeSpecificPart.toString()
                        )!!
                        app.uninstalled = true
                        app.uninstallDate = currentDate.time

                        applicationRepository.update(app)

                        withContext(Dispatchers.Main) {
                            notificationBuilderAction.setDefaults(Notification.DEFAULT_ALL)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setAutoCancel(true)
                                .setTicker("PObY-A")
                                .setContentTitle("Application removed")
                                .setContentText(app.name)
                                .setContentInfo("Info")

                            notificationManager.notify(2, notificationBuilderAction.build())
                        }


                    }
                }
            }

        }



        try {
            registerReceiver(receiver, intentFilter)

        } catch (e: Exception) {
            Log.e(TAG, e.stackTrace.toString())
        }

        return START_STICKY
    }


    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTrace.toString())
            return super.onTaskRemoved(rootIntent)
        }


        val restartServiceIntent =
            Intent(applicationContext, ApplicationsService::class.java).also {
                it.setPackage(packageName)
            }

        startForegroundService(restartServiceIntent)

        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {

        try {
            unregisterReceiver(receiver)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.stackTrace.toString())
        } finally {
            super.onDestroy()
        }

        val restartServiceIntent =
            Intent(applicationContext, ApplicationsService::class.java).also {
                it.setPackage(packageName)
            }

        startForegroundService(restartServiceIntent)


    }

}