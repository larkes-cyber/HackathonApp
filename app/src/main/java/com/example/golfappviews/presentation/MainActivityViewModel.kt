package com.example.golfappviews.presentation

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor():ViewModel() {

    private val _stateUI = MutableLiveData<Bitmap?>(null)
    val stateUI:LiveData<Bitmap?> = _stateUI

    fun onFrameChange(frame:Bitmap){
        _stateUI.value = frame
    }

}