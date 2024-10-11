package com.project_aurora.emu.compositor

import android.content.Context
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.Bitmap
import com.project_aurora.emu.NativeCode


class VulkanSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {
    var logPaint = Paint().apply {
        color = Color.WHITE
        textSize = 18f
        style = Paint.Style.FILL
    }
    
    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        onDraw(holder) { state ->
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        NativeCode().surfaceChanged(holder.surface, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        NativeCode().surfaceDestroyed()
    }
    
    fun onDraw(holder: SurfaceHolder, callback: (String) -> Unit) {
        var canvas: Canvas = holder.lockCanvas()
        
        canvas.let { c ->
            callback.invoke("DrawCalls Initialized")
            c.drawText("API: Vulkan", 100f, 100f, logPaint)
            holder.unlockCanvasAndPost(c)
            callback.invoke("DrawCalls Terminated")
        }
    }

}