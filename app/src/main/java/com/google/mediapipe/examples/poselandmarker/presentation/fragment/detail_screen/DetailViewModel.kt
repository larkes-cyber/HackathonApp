package com.google.mediapipe.examples.poselandmarker.presentation.fragment.detail_screen

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.examples.poselandmarker.domain.repository.GolfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class DetailViewModel@Inject constructor(
    private val golfRepository: GolfRepository
): ViewModel() {

    private val _detailUIState = MutableLiveData(DetailScreenState())
    val detailUIState:LiveData<DetailScreenState> = _detailUIState



    fun setup(){
        viewModelScope.launch {
            _detailUIState.value = DetailScreenState(isLoading = true)
            val coords = arrayListOf<ArrayList<ArrayList<Float>>>()
            golfRepository.getAllCoords().forEach {
                coords.add(it)
            }


            val ids = golfRepository.sendAllCoords(coords)
            Log.d("sdfgfdsdf",ids.toString())
            val frames = ids.map { golfRepository.getFrameById(it) }

            _detailUIState.value = DetailScreenState(
                frames = frames,
                isLoading = false
            )



        }
    }



}


