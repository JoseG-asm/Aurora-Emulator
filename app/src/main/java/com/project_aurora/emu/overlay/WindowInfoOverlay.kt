package com.project_aurora.emu.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.graphics.PixelFormat
import android.view.Gravity
import android.widget.FrameLayout

class WindowInfoOverlay(ctx: Context) {
    private var WindowInfoView : View? = null
    private val context = ctx
    init {
    
    }
    
    fun drawWindowInfoView() {
        WindowInfoView = View(context).apply {
            setBackgroundColor(Color.argb(150,30,0,0))
            val params = WindowManager.LayoutParams(
                150,
                150,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
            }
                layoutParams = params
        }
        
        val windowManager = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).apply {
            addView(WindowInfoView, WindowInfoView?.layoutParams)
        }
    }
}