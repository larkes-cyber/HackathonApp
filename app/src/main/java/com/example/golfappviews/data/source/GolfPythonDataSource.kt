package com.example.golfappviews.data.source

import kotlinx.coroutines.flow.Flow

interface GolfPythonDataSource {

    suspend fun sendBytes(byteArray: ByteArray): Flow<String>
    suspend fun sendFrame(byteArray: ByteArray):ByteArray

}