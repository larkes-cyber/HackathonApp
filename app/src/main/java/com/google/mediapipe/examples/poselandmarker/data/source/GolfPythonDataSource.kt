package com.google.mediapipe.examples.poselandmarker.data.source

import com.google.mediapipe.examples.poselandmarker.domain.model.FrameResponse

interface GolfPythonDataSource {

    suspend fun sendFrame(data:ArrayList<ArrayList<Float>>):FrameResponse
    suspend fun sendAllCoords(data:ArrayList<ArrayList<ArrayList<Float>>>):List<Int>
    suspend fun getAnalise(frames:List<ByteArray>, coords:ArrayList<ArrayList<ArrayList<Float>>>):ByteArray?

}