package com.wexmarket.android.barcode

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.annotation.WorkerThread
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

/**
 * Created by lulingzhi on 2017/11/21.
 */

private val BITMAP_QR_HINTS = mapOf<DecodeHintType, Any>(
        Pair(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE),
        Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE)),
        Pair(DecodeHintType.CHARACTER_SET, "utf-8")
)

@WorkerThread
fun decodeBitmapQr(bitmap: Bitmap): Result? {
    val w = bitmap.width
    val h = bitmap.height
    val pixels = IntArray(w * h)
    bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
    val result = MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(RGBLuminanceSource(w, h, pixels))), BITMAP_QR_HINTS)
    if (result.text == null) {
        throw IllegalStateException()
    }
    return result
}

fun getDecodeFriendlyBitmapFromUri(uri: Uri, contentResolver: ContentResolver, expectedSize: Int = 400): Bitmap {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    contentResolver.openInputStream(uri).use {
        BitmapFactory.decodeStream(it, null, options)
    }
    var sampleSize = minOf(options.outWidth, options.outHeight) / expectedSize
    if (sampleSize <= 0) {
        sampleSize = 1
    }
    options.inSampleSize = sampleSize
    options.inJustDecodeBounds = false
    return contentResolver.openInputStream(uri).use {
        BitmapFactory.decodeStream(it, null, options)
    }
}