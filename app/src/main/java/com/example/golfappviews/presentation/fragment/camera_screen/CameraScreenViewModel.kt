package com.example.golfappviews.presentation.fragment.camera_screen

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golfappviews.domain.repository.GolfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val golfRepository: GolfRepository
):ViewModel() {

    private val _streamState = MutableLiveData<Bitmap?>(null)
    val streamState:LiveData<Bitmap?> = _streamState

    fun onFrameChange(frame:ByteArray){
        viewModelScope.launch {
            val res = golfRepository.sendFrame(frame)
            _streamState.value = res
        }

    }

}