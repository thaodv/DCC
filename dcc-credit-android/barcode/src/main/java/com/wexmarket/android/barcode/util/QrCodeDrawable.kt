package com.wexmarket.android.barcode.util

import android.graphics.*
import android.graphics.drawable.Drawable
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.Encoder
import com.google.zxing.qrcode.encoder.QRCode

/**
 * Created by lulingzhi on 2017/11/21.
 */
class QrCodeDrawable : Drawable() {

    var qrcodeBitmap: Bitmap? = null
        set(value) {
            field = value
            invalidateSelf()
        }

    fun setQrContent(content: String) {
        qrcodeBitmap = encodeQrCode(content).toBitmap(quietZone)
    }

    var quietZone = 2

    var roundCorner = true

    private val bitmapPaint = Paint().apply {
        this.isAntiAlias = false
        this.isFilterBitmap = false
        this.isDither = false
    }

    @Transient
    private val srcRect = Rect()
    @Transient
    private val dstRect = Rect()

    @Transient
    private val path = Path()

    /**
     * @see [com.google.zxing.qrcode.QRCodeWriter.renderResult]
     */
    override fun draw(canvas: Canvas?) {
        canvas ?: return
        val bitmap = qrcodeBitmap
        bitmap?.let {
            val bounds = bounds

            val qrWidth = it.width
            val qrHeight = it.height
            val outputWidth = Math.max(bounds.width(), qrWidth)
            val outputHeight = Math.max(bounds.height(), qrHeight)
            val multiple: Int = Math.min(outputWidth / qrWidth, outputHeight / qrHeight)
            val cx = bounds.centerX()
            val cy = bounds.centerY()
            srcRect.set(0, 0, qrWidth, qrHeight)
            val l = cx - qrWidth * multiple / 2
            val t = cy - qrHeight * multiple / 2
            dstRect.set(l, t, l + qrWidth * multiple, t + qrHeight * multiple)

            if(roundCorner) {
                canvas.save()
                path.reset()
                val radius = quietZone*multiple.toFloat()
                path.addRoundRect(RectF(dstRect),radius,radius,Path.Direction.CW)
                canvas.clipPath(path)
                canvas.drawBitmap(it, srcRect, dstRect, bitmapPaint)
                canvas.restore()
            }else{
                canvas.drawBitmap(it, srcRect, dstRect, bitmapPaint)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        bitmapPaint.alpha = alpha
    }

    override fun getAlpha(): Int {
        return bitmapPaint.alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE // opaque only

    override fun setColorFilter(colorFilter: ColorFilter?) {
        bitmapPaint.colorFilter = colorFilter
    }

    companion object {
        const val DEFAULT_QUIET_ZONE = 4
        const val DEFAULT_PRESENT_COLOR = Color.BLACK
        const val DEFAULT_ABSENT_COLOR = Color.WHITE
        const val PRESENT_BYTE = 1.toByte()
        val qrWriter = QRCodeWriter()

        fun encodeQrCode(content: String) = Encoder.encode(content, ErrorCorrectionLevel.L)

        fun encodeQrCodeBitmap(content: String) = encodeQrCode(content).toBitmap()

        fun encodeQrCodeBitmap(content: String, width: Int, height: Int): Bitmap {
            val bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, width, height)

            val w = bitMatrix.width
            val h = bitMatrix.height
            val colors = IntArray(w * h).apply {
                (0 until h * w).forEach {
                    val x = it % w
                    val y = it / w
                    this[it] = if (bitMatrix.get(x, y))
                        DEFAULT_PRESENT_COLOR
                    else
                        DEFAULT_ABSENT_COLOR
                }
            }
            return Bitmap.createBitmap(colors, w, h, Bitmap.Config.ARGB_8888)
        }

        fun QRCode.toBitmap(quietZone: Int = DEFAULT_QUIET_ZONE): Bitmap {
            val matrix = this.matrix
            val width = matrix.width
            val totalWidth = width + quietZone * 2
            val height = matrix.height
            val totalHeight = height + quietZone * 2
            val colors = IntArray(totalWidth * totalHeight){
                val x = it % totalWidth - quietZone
                val y = it / totalWidth - quietZone
                if (x in 0..(width - 1) && y in 0..(height - 1) && matrix[x, y] == PRESENT_BYTE)
                    DEFAULT_PRESENT_COLOR
                else
                    DEFAULT_ABSENT_COLOR
            }
            return Bitmap.createBitmap(colors, totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        }
    }
}