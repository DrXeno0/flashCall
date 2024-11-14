package com.nocturnal.connectiontester.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat

class phoneServices : Service() {
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var isFlashOn = false
    private var isBlinking = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate() {
        super.onCreate()


        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]


        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)


        startForegroundService()
    }

    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {

                    if (isBlinking) startBlinkingFlashlight()
                }
                TelephonyManager.CALL_STATE_IDLE, TelephonyManager.CALL_STATE_OFFHOOK -> {

                    stopBlinkingFlashlight()
                }
            }
        }
    }


    private fun startBlinkingFlashlight() {
        isBlinking = true
        toggleFlashlight()
    }


    private fun stopBlinkingFlashlight() {
        isBlinking = false
        handler.removeCallbacksAndMessages(null)
        turnOffFlashlight()
    }


    private fun toggleFlashlight() {
        if (!isBlinking) return

        if (isFlashOn) {
            turnOffFlashlight()
        } else {
            turnOnFlashlight()
        }

        handler.postDelayed({ toggleFlashlight() }, 500)
    }

    private fun turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true)
            isFlashOn = true
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false)
            isFlashOn = false
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun startForegroundService() {
        val notificationChannelId = "flashlight_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Flashlight Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Flashlight Service")
            .setContentText("Flashlight will blink during incoming calls.")
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopBlinkingFlashlight()
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}