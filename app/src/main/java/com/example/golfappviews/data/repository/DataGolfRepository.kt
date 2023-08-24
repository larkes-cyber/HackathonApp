package com.example.golfappviews.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.golfappviews.data.source.GolfPythonDataSource
import com.example.golfappviews.domain.repository.GolfRepository
import com.example.golfappviews.util.InternetConnectionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.File

class DataGolfRepository(
    private val golfPythonDataSource: GolfPythonDataSource
):GolfRepository {

    override suspend fun sendFrame(byteArray: ByteArray): Bitmap{
        val img = golfPythonDataSource.sendFrame(byteArray)
        return BitmapFactory.decodeByteArray(img, 0, img.size)
    }
}