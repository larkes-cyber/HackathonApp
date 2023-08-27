package com.google.mediapipe.examples.poselandmarker.presentation.fragment.camera_screen

data class FrameState(
    val hitStarted:Boolean = false,
    val hitEnded:Boolean = false,
    val rotate:String = "Не определено",
    val checkPerson:Boolean = false,
    val autoRecord:Boolean = false,
    val videoMode:Boolean = false
)