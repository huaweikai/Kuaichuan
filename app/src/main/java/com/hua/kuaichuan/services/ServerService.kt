package com.hua.kuaichuan.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.hua.kuaichuan.R
import com.hua.kuaichuan.others.Contacts
import com.hua.kuaichuan.others.ServerState
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

/**
 * @author : huaweikai
 * @Date   : 2021/12/15
 * @Desc   : 服务器的服务
 */
private const val TAG = "ServerService"
class ServerService :Service() {
    private lateinit var server:Server

    private var serverOff = true


    private val _serverState = MutableStateFlow<ServerState>(ServerState.Close)
    val serverState get() =  _serverState

    private lateinit var notification: Notification
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent:PendingIntent

    inner class ServiceBinder: Binder() {
        val service = this@ServerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return ServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()
        val intent = Intent(this,ServerService::class.java).also {
            it.action = "stopService"
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
        }else{
            pendingIntent = PendingIntent.getService(this,0,intent,0)
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotification()
        }else{
            createMiniNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                "stopService" ->{
                    server.shutdown()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun openOrOffServer(port: Int){
        if(serverOff){
            server = AndServer.webServer(this)
                .port(port)
                .listener(serverListener)
                .timeout(10, TimeUnit.SECONDS)
                .build()
            server.startup()
            serverOff = false
        }else{
            server.shutdown()
            serverOff = true
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(){
        val notificationChannel = NotificationChannel(
                Contacts.NOTIFICATION_CHANNEL,
                Contacts.NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notification = NotificationCompat
                .Builder(this,Contacts.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("web服务器正在运行")
                .addAction(R.drawable.ic_stop,"停止",pendingIntent)
                .build()
            notificationManager.createNotificationChannel(notificationChannel)
        }

    private fun createMiniNotification() {
        notification = NotificationCompat
            .Builder(this,Contacts.NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(R.drawable.ic_stop,"停止",pendingIntent)
            .build()
    }

    private val serverListener = object :Server.ServerListener{
        override fun onStarted() {
            _serverState.value = ServerState.Complete
            startForeground(5,notification)
        }

        override fun onStopped() {
            _serverState.value = ServerState.Close
            stopForeground(true)
        }

        override fun onException(e: Exception?) {
            _serverState.value = ServerState.ServerError(e?.message)
        }
    }
    fun getServerPort()=server.port

    fun getServerIsRunning() = server.isRunning
}