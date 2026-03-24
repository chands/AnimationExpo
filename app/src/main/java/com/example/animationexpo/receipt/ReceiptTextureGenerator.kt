package com.example.animationexpo.receipt

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

object ReceiptTextureGenerator {

    private const val TEX_WIDTH = 1024
    private const val TEX_HEIGHT = 2048
    private const val SCALE = 2f
    private const val LOGICAL_W = TEX_WIDTH / SCALE
    private const val LOGICAL_H = TEX_HEIGHT / SCALE

    fun generate(): Bitmap {
        val bitmap = Bitmap.createBitmap(TEX_WIDTH, TEX_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.scale(SCALE, SCALE)

        val bgPaint = Paint().apply { color = 0xFFF8F8F4.toInt() }
        canvas.drawRect(0f, 0f, LOGICAL_W, LOGICAL_H, bgPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF1A1A1A.toInt()
            typeface = Typeface.MONOSPACE
        }

        drawHeader(canvas, textPaint)
        drawMeta(canvas, textPaint)
        drawDivider(canvas, textPaint, 320f)
        drawItems(canvas, textPaint)
        drawDivider(canvas, textPaint, 610f)
        drawSubtotalAndTax(canvas, textPaint)
        drawTotalDivider(canvas)
        drawTotal(canvas, textPaint)
        drawFooter(canvas, textPaint)

        return bitmap
    }

    private fun drawHeader(canvas: Canvas, paint: Paint) {
        paint.textSize = 36f
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("THE FLORNRM SHOP", LOGICAL_W / 2f, 80f, paint)

        paint.textSize = 22f
        paint.isFakeBoldText = false
        canvas.drawText("42 Mesh Lane, WebGL City", LOGICAL_W / 2f, 125f, paint)
        canvas.drawText("Tel: (555) 042-1337", LOGICAL_W / 2f, 160f, paint)
    }

    private fun drawMeta(canvas: Canvas, paint: Paint) {
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 22f
        canvas.drawText("Date: 2026-02-23  14:17", 40f, 230f, paint)
        canvas.drawText("Order: #00382", 40f, 270f, paint)
    }

    private fun drawDivider(canvas: Canvas, paint: Paint, y: Float) {
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 22f
        canvas.drawText("- - - - - - - - - - - - - - - - - -", LOGICAL_W / 2f, y, paint)
    }

    private fun drawItems(canvas: Canvas, paint: Paint) {
        val startY = 380f
        val lineH = 45f
        paint.textSize = 22f

        val items = listOf(
            "Vertex Shader" to "$4.20",
            "Fragment Shader" to "$3.50",
            "Normal Map" to "$2.80",
            "UV Unwrap" to "$1.50",
            "Cloth Simulation" to "$6.00"
        )

        items.forEachIndexed { index, (name, price) ->
            val y = startY + lineH * index
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(name, 40f, y, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(price, LOGICAL_W - 40f, y, paint)
        }
    }

    private fun drawSubtotalAndTax(canvas: Canvas, paint: Paint) {
        paint.textSize = 22f

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Subtotal", 40f, 670f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("$18.00", LOGICAL_W - 40f, 670f, paint)

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Tax (8%)", 40f, 715f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("$1.44", LOGICAL_W - 40f, 715f, paint)
    }

    private fun drawTotalDivider(canvas: Canvas) {
        val linePaint = Paint().apply { color = 0xFF1A1A1A.toInt() }
        canvas.drawRect(40f, 755f, LOGICAL_W - 40f, 760f, linePaint)
    }

    private fun drawTotal(canvas: Canvas, paint: Paint) {
        paint.textSize = 28f
        paint.isFakeBoldText = true

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("TOTAL", 40f, 815f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("$19.44", LOGICAL_W - 40f, 815f, paint)

        paint.isFakeBoldText = false
    }

    private fun drawFooter(canvas: Canvas, paint: Paint) {
        paint.textSize = 22f
        paint.textAlign = Paint.Align.CENTER
        paint.color = 0xFF1A1A1A.toInt()
        canvas.drawText("Thank you for visiting!", LOGICAL_W / 2f, 920f, paint)

        paint.textSize = 18f
        paint.color = 0xFF555555.toInt()
        canvas.drawText("github.com/flornkm", LOGICAL_W / 2f, 960f, paint)
    }
}
