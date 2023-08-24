package com.example.golfappviews.data.source

import android.util.Log
import com.chaquo.python.Python
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class GolfPythonDataSourceImpl(
    private val pythonInstance:Python
):GolfPythonDataSource {

    private val pyModule = pythonInstance.getModule("script")

    override suspend fun sendBytes(byteArray: ByteArray): Flow<String> = callbackFlow {
        val sendMessFunc = pyModule["func"]
        sendMessFunc?.call(byteArray)!!.onEach {
            trySend(it.toString())
            channel.close()
        }
        awaitClose {  }
    }

    override suspend fun sendFrame(byteArray: ByteArray): ByteArray {
        val sendMessFrame = pyModule["frame"]
        val res = sendMessFrame?.call(byteArray).toString()
        return byteArray
    }
}