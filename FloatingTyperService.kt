package com.afmir.typerboard

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class FloatingTyperService : Service() {
    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        TyperPrefs.load(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val view = LayoutInflater.from(this).inflate(R.layout.overlay_panel, null)
        overlayView = view

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 120
        }

        val btnNums = view.findViewById<Button>(R.id.ovBtnNums)
        val btnAlpha = view.findViewById<Button>(R.id.ovBtnAlpha)
        val btnWords = view.findViewById<Button>(R.id.ovBtnWords)
        val btnIphone = view.findViewById<Button>(R.id.ovBtnIphone)
        val btnClose = view.findViewById<TextView>(R.id.ovBtnClose)

        fun refreshLabels() {
            btnNums.text = if (TyperPrefs.autoNums) "On" else "Off"
            btnAlpha.text = if (TyperPrefs.autoAlpha) "On" else "Off"
            btnWords.text = if (TyperPrefs.autoWords) "On" else "Off"
            btnIphone.text = if (TyperPrefs.iphoneDrop) "On" else "Off"
        }
        refreshLabels()

        btnNums.setOnClickListener {
            TyperPrefs.autoNums = !TyperPrefs.autoNums
            TyperPrefs.save(this)
            refreshLabels()
        }
        btnAlpha.setOnClickListener {
            TyperPrefs.autoAlpha = !TyperPrefs.autoAlpha
            TyperPrefs.save(this)
            refreshLabels()
        }
        btnWords.setOnClickListener {
            TyperPrefs.autoWords = !TyperPrefs.autoWords
            TyperPrefs.save(this)
            refreshLabels()
        }
        btnIphone.setOnClickListener {
            TyperPrefs.iphoneDrop = !TyperPrefs.iphoneDrop
            TyperPrefs.save(this)
            refreshLabels()
        }
        btnClose.setOnClickListener { stopSelf() }

        view.setOnTouchListener(object : View.OnTouchListener {
            private var startX = 0
            private var startY = 0
            private var touchX = 0f
            private var touchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = params.x
                        startY = params.y
                        touchX = event.rawX
                        touchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = startX + (event.rawX - touchX).toInt()
                        params.y = startY + (event.rawY - touchY).toInt()
                        windowManager.updateViewLayout(view, params)
                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(view, params)
    }

    override fun onDestroy() {
        overlayView?.let {
            if (it.isAttachedToWindow) windowManager.removeView(it)
        }
        overlayView = null
        super.onDestroy()
    }
}
