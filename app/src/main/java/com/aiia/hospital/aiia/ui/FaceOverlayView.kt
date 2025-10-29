package com.aiia.hospital.aiia.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class FaceOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val boxPaint = Paint().apply {
        color = 0xFFFF9800.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val boxes = mutableListOf<RectF>()

    fun setBoxes(list: List<RectF>) {
        boxes.clear()
        boxes.addAll(list)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        boxes.forEach { canvas.drawRect(it, boxPaint) }
    }
}