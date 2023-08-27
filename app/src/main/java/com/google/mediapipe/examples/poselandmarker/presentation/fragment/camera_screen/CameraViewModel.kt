package com.google.mediapipe.examples.poselandmarker.presentation.fragment.camera_screen

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.examples.poselandmarker.domain.repository.GolfRepository
import com.google.mediapipe.examples.poselandmarker.presentation.PoseLandmarkerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val golfRepository: GolfRepository
):ViewModel() {
    private var _model = PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_FULL
    private var _delegate: Int = PoseLandmarkerHelper.DELEGATE_CPU
    private var _minPoseDetectionConfidence: Float =
        PoseLandmarkerHelper.DEFAULT_POSE_DETECTION_CONFIDENCE
    private var _minPoseTrackingConfidence: Float =
        PoseLandmarkerHelper.DEFAULT_POSE_TRACKING_CONFIDENCE
    private var _minPosePresenceConfidence: Float =
        PoseLandmarkerHelper.DEFAULT_POSE_PRESENCE_CONFIDENCE


    private val _collectedFrames = MutableLiveData<MutableList<List<List<Float>>>>(mutableListOf())
    val collectedFrames:LiveData<MutableList<List<List<Float>>>> = _collectedFrames

    private val _frameState = MutableLiveData(FrameState())
    val frameState:LiveData<FrameState> = _frameState

    private val _currentFrameState = MutableLiveData<Bitmap?>()
    //val currentFrameState:LiveData<Bitmap?> = _c


     val _end = MutableLiveData(false)

    fun switchRecording(){
        if(frameState.value!!.hitStarted){
            _frameState.value = frameState.value!!.copy(hitEnded = true)
        }
        if(!frameState.value!!.hitStarted){
            _frameState.value = frameState.value!!.copy(hitStarted = true)
        }

    }

    fun onAutoChange(){
        _frameState.value = frameState.value!!.copy(autoRecord = !_frameState.value!!.autoRecord)
    }

    fun onCurrentFrameChange(bitmap: Bitmap?){
        if(frameState.value!!.hitStarted){
            golfRepository.cacheFrame(bitmap!!)
        }
    }

    fun onCache(bitmap: Bitmap){
        golfRepository.cacheFrame(bitmap)
    }

    fun onVideoMode(){
        _frameState.value = frameState.value!!.copy(videoMode = true)
    }

    fun onCacheCoords(data: ArrayList<ArrayList<Float>>){
        golfRepository.cacheCoords(data)
    }

    fun end(){

    }

    fun onFrameStream(data: ArrayList<ArrayList<Float>>){

        viewModelScope.launch {
            val res = golfRepository.sendFrame(data)
            Log.d("werfgfewert", "startedRec:${res.hitStarted} check:${res.checkPerson}")

            if(frameState.value!!.autoRecord) {

                if (res.hitStarted) {
                    _frameState.value = FrameState(
                        hitStarted = true,
                        hitEnded = false,
                        checkPerson = res.checkPerson,
                        rotate = res.rotate,
                        autoRecord = true
                    )
                }
                if (res.hitEnded && frameState.value!!.hitStarted) {
                    _frameState.value = FrameState(
                        hitStarted = false,
                        hitEnded = true,
                        checkPerson = res.checkPerson,
                        rotate = res.rotate,
                        autoRecord = true
                    )
                }
            }
            _frameState.value = frameState.value!!.copy(rotate = res.rotate, checkPerson = res.checkPerson)
            if(frameState.value!!.hitStarted){
                golfRepository.cacheCoords(data)
            }
        }
    }


    val currentDelegate: Int get() = _delegate
    val currentModel: Int get() = _model
    val currentMinPoseDetectionConfidence: Float
        get() =
            _minPoseDetectionConfidence
    val currentMinPoseTrackingConfidence: Float
        get() =
            _minPoseTrackingConfidence
    val currentMinPosePresenceConfidence: Float
        get() =
            _minPosePresenceConfidence

    fun setDelegate(delegate: Int) {
        _delegate = delegate
    }

    fun setMinPoseDetectionConfidence(confidence: Float) {
        _minPoseDetectionConfidence = confidence
    }

    fun setMinPoseTrackingConfidence(confidence: Float) {
        _minPoseTrackingConfidence = confidence
    }

    fun setMinPosePresenceConfidence(confidence: Float) {
        _minPosePresenceConfidence = confidence
    }

    fun setModel(model: Int) {
        _model = model
    }
}