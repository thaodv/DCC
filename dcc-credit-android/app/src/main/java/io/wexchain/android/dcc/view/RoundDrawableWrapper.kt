package io.wexchain.android.dcc.view

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

/**
 * Created by sisel on 2018/1/31.
 */
class RoundDrawableWrapper(bitmap: Bitmap) : Drawable() {

    constructor(drawable: Drawable) : this(drawableToBitmap(drawable))

    private val rBitmapShader: BitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    private val rBitmapPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val rShaderMatrix = Matrix()

    @Transient
    private val srcRect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
    @Transient
    private val dstRect = RectF()


    override fun draw(canvas: Canvas?) {
        canvas ?: return
        val b = bounds
        val radius = minOf(b.width() * 0.5f, b.height() * 0.5f) - 1f
        val cx = b.exactCenterX()
        val cy = b.exactCenterY()
        dstRect.set(cx - radius, cy - radius, cx + radius, cy + radius)
        rShaderMatrix.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.FILL)
        rBitmapShader.setLocalMatrix(rShaderMatrix)
        rBitmapPaint.shader = rBitmapShader
        canvas.drawRoundRect(dstRect, radius, radius, rBitmapPaint)
    }

    override fun setAlpha(alpha: Int) {
        rBitmapPaint.alpha = alpha
        invalidateSelf()
    }

    override fun getAlpha(): Int {
        return rBitmapPaint.alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        rBitmapPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    companion object {
        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }

            val width = drawable.intrinsicWidth.coerceIn(2, 2160)
            val height = drawable.intrinsicHeight.coerceIn(2, 2160)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
    }
}