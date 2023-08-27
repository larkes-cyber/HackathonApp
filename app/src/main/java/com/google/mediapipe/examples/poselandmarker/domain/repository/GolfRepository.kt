package com.google.mediapipe.examples.poselandmarker.domain.repository

import android.graphics.Bitmap
import com.google.mediapipe.examples.poselandmarker.domain.model.FrameResponse
import com.google.mediapipe.formats.proto.LandmarkProto

interface GolfRepository {

    suspend fun sendFrame(data:ArrayList<ArrayList<Float>>):FrameResponse
    suspend fun sendAllCoords(data:ArrayList<ArrayList<ArrayList<Float>>>):List<Int>
    fun cacheFrame(frame:Bitmap)
    fun cacheCoords(data:ArrayList<ArrayList<Float>>)
    fun clearFrameCacheStorage()
    fun getFrameById(id:Int):Bitmap
    fun getAllCoords():ArrayList<ArrayList<ArrayList<Float>>>
    suspend fun getAnalise(frames:List<ByteArray>, coords:ArrayList<ArrayList<ArrayList<Float>>>):Bitmap?


}