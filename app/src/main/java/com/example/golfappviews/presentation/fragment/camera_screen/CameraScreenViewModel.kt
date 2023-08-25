package com.example.golfappviews.presentation.fragment.camera_screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golfappviews.domain.repository.GolfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val golfRepository: GolfRepository,
):ViewModel() {




    private val _streamImage = MutableLiveData<Bitmap?>(null)
    val streamImage:LiveData<Bitmap?> = _streamImage

    private val _videoStatus = MutableLiveData<Boolean?>(null)
    val videoStatus:LiveData<Boolean?> = _videoStatus


    fun onFrameChange(frame:ByteArray){
//        viewModelScope.launch {
//            val res = golfRepository.sendFrame(frame)
//            _streamImage.value = res
//        }
        var bitmap = BitmapFactory.decodeByteArray(frame, 0, frame.size)
        //  val res = golfRepository.sendFrame(frame)
        val matrix = Matrix()
        matrix.postRotate(90f)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
        val rotatedBitmap = Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        )
        _streamImage.value = rotatedBitmap
    }

    fun switchVideoStatus(){
        if(videoStatus.value == null) {
            _videoStatus.value = true
            return
        }
        _videoStatus.value = !videoStatus.value!!
    }

}