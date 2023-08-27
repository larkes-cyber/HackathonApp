package com.google.mediapipe.examples.poselandmarker.data.source

import android.graphics.Bitmap
import android.util.Log

class FrameImageDataSourceImpl:FrameImageDataSource {

    private val coordsStorage:ArrayList<ArrayList<ArrayList<Float>>> = arrayListOf()
    private val framesStorage:ArrayList<Bitmap> = arrayListOf()

    override fun cacheFramesCoords(coords: ArrayList<ArrayList<Float>>) {
        coordsStorage.add(coords)
    }

    override fun cacheFrame(bitmap: Bitmap) {
        framesStorage.add(bitmap)
    }

    override fun clearFrameCacheStorage() {
        coordsStorage.clear()
        framesStorage.clear()
    }

    override fun getFrameById(id: Int): Bitmap {
        framesStorage.forEach {
            Log.d("efgfdedfg",it.toString())
        }
        Log.d("sdfgfdsdfd",framesStorage.size.toString())
        return framesStorage[id]
    }

    override fun getAllCoords(): ArrayList<ArrayList<ArrayList<Float>>> {
        return coordsStorage
    }


}