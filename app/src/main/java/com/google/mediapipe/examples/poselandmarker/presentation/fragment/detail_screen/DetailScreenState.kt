package com.google.mediapipe.examples.poselandmarker.presentation.fragment.detail_screen

import android.graphics.Bitmap

class DetailScreenState(
    val frames:List<Bitmap> = listOf(),
    val errorFrame:Bitmap? = null,
    val isLoading:Boolean = false
)