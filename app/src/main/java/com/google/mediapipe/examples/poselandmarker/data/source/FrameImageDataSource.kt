package com.google.mediapipe.examples.poselandmarker.data.source

import android.graphics.Bitmap

interface FrameImageDataSource {

    fun cacheFramesCoords(coords:ArrayList<ArrayList<Float>>)
    fun cacheFrame(bitmap: Bitmap)
    fun clearFrameCacheStorage()
    fun getFrameById(id:Int):Bitmap
    fun getAllCoords():ArrayList<ArrayList<ArrayList<Float>>>

}