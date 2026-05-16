package com.duc.offlinemusicplayer.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : Service() {

    companion object {
        private const val CHANNEL_ID = "music_playback"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_PLAY = "com.duc.offlinemusicplayer.action.PLAY"
        const val ACTION_PAUSE = "com.duc.offlinemusicplayer.action.PAUSE"
        const val ACTION_NEXT = "com.duc.offlinemusicplayer.action.NEXT"
        const val ACTION_PREVIOUS = "com.duc.offlinemusicplayer.action.PREVIOUS"
    }

    @Inject
    lateinit var playbackRepository: PlaybackRepository

    override fun onCreate() {
        super.onCreate()
        createNotificationChannelIfNeeded()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> launchRepositoryAction { playbackRepository.play() }
            ACTION_PAUSE -> launchRepositoryAction { playbackRepository.pause() }
            ACTION_NEXT -> launchRepositoryAction { playbackRepository.skipNext() }
            ACTION_PREVIOUS -> launchRepositoryAction { playbackRepository.skipPrevious() }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW,
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Playing music")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun launchRepositoryAction(action: suspend () -> Unit) {
        Thread {
            kotlinx.coroutines.runBlocking {
                action()
            }
        }.start()
    }
}