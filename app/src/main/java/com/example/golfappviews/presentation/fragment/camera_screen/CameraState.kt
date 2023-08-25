package com.example.golfappviews.presentation.fragment.camera_screen

import android.graphics.Bitmap

data class CameraState(
    val streamImage:Bitmap? = null,
    val videoStatus:Boolean = false
)