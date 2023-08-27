package com.google.mediapipe.examples.poselandmarker.data.source

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.chaquo.python.Python
import com.google.mediapipe.examples.poselandmarker.domain.model.FrameResponse
import java.util.*
import kotlin.collections.ArrayList

class GolfPythonDataSourceImpl(
    private val python: Python
):GolfPythonDataSource{

    private val pyModule = python.getModule("model_online")
    private val pyModuleVideo = python.getModule("model_video")

    override suspend fun sendFrame(array: ArrayList<ArrayList<Float>>): FrameResponse {
        if(array.size == 0) return FrameResponse(
            rotate = "" ,
            hitStarted = false,
            hitEnded = false,
            checkPerson = false
        )
        val funcFirstPersonCheck = pyModule["check_person"]
        val funcGetStreamRacurs = pyModule["racurs_model"]
        val funcVideoStatus = pyModule["video_model"]
        val arr = array.map { it.toFloatArray() }.toTypedArray()
        val resFuncFirstPersonCheck = funcFirstPersonCheck!!.call(arr).toBoolean()
        if(resFuncFirstPersonCheck){
            val rotate = funcGetStreamRacurs!!.call(arr).toInt()
            val videoStatus = funcVideoStatus!!.call(arr).toInt()


            return FrameResponse(
                rotate = if(rotate == 0) "Снимают спереди" else if(rotate == 1) "Снимают слева" else "Снимают справа" ,
                hitStarted = videoStatus == 1,
                hitEnded = videoStatus == 2,
                checkPerson = true
            )
        }else{
            return FrameResponse(
                rotate = "" ,
                hitStarted = false,
                hitEnded = false,
                checkPerson = false
            )
        }

    }

    override suspend fun sendAllCoords(data: ArrayList<ArrayList<ArrayList<Float>>>): List<Int> {
        val res = pyModuleVideo["pose_codr_model"]!!.call(data.map { arr -> arr.map { it.toArray() }.toTypedArray() }.toTypedArray())
        val arr = res.asList().map { it.toInt() }
        return arr
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAnalise(
        frames: List<ByteArray>,
        coords: ArrayList<ArrayList<ArrayList<Float>>>,
    ): ByteArray? {
        val res = pyModuleVideo["pose_bytearray_model"]!!.call(
            frames.toTypedArray(),
            coords.map { arr -> arr.map { it.toArray() }.toTypedArray() }.toTypedArray()
        ).toString()
        Log.d("sdfgfdsdfgfd",res)
        if(res.isEmpty()) return null
        return Base64.getDecoder().decode(res)


    }

}