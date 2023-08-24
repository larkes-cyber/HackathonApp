package com.example.golfappviews.domain.repository

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow


interface GolfRepository {

    suspend fun sendFrame(byteArray: ByteArray):Bitmap

}