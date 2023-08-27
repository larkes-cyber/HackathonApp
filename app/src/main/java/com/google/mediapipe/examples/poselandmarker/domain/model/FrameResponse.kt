package com.google.mediapipe.examples.poselandmarker.domain.model

data class FrameResponse(
    val hitStarted:Boolean,
    val hitEnded:Boolean,
    val rotate:String,
    val checkPerson:Boolean
)