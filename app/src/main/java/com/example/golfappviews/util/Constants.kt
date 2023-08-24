package com.example.golfappviews.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object Constants {
    const val APP_NAME = "GolfApp"
    const val VIDEO_FILE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
}

fun Bitmap.toByteArray():ByteArray{
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 90, stream)
    return stream.toByteArray()
}