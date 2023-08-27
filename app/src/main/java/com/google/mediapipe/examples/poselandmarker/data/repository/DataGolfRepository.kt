package com.google.mediapipe.examples.poselandmarker.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.mediapipe.examples.poselandmarker.data.source.FrameImageDataSource
import com.google.mediapipe.examples.poselandmarker.data.source.GolfPythonDataSource
import com.google.mediapipe.examples.poselandmarker.domain.model.FrameResponse
import com.google.mediapipe.examples.poselandmarker.domain.repository.GolfRepository

class DataGolfRepository(
    private val pythonDataSource: GolfPythonDataSource,
    private val frameImageDataSource: FrameImageDataSource
):GolfRepository {
    override suspend fun sendFrame(data: ArrayList<ArrayList<Float>>): FrameResponse {
       return pythonDataSource.sendFrame(data)
    }

    override suspend fun sendAllCoords(data: ArrayList<ArrayList<ArrayList<Float>>>): List<Int> {
       return pythonDataSource.sendAllCoords(data)
    }

    override fun cacheFrame(frame: Bitmap) {
        frameImageDataSource.cacheFrame(frame)
    }

    override fun cacheCoords(data: ArrayList<ArrayList<Float>>) {
        frameImageDataSource.cacheFramesCoords(data)
    }

    override fun clearFrameCacheStorage() {
        frameImageDataSource.clearFrameCacheStorage()
    }

    override fun getFrameById(id: Int): Bitmap {
        return frameImageDataSource.getFrameById(id)
    }

    override fun getAllCoords(): ArrayList<ArrayList<ArrayList<Float>>> {
        return frameImageDataSource.getAllCoords()
    }

    override suspend fun getAnalise(
        frames: List<ByteArray>,
        coords: ArrayList<ArrayList<ArrayList<Float>>>,
    ): Bitmap? {
        val bytes = pythonDataSource.getAnalise(frames, coords) ?: return null
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


}