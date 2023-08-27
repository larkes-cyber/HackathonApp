package com.google.mediapipe.examples.poselandmarker.presentation.fragment.detail_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModelProvider
import com.google.mediapipe.examples.poselandmarker.R
import com.google.mediapipe.examples.poselandmarker.presentation.fragment.camera_screen.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailScreenFragment : Fragment() {


    lateinit var viewModel:DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        viewModel.setup()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val imageView0 = view.findViewById<ImageView>(R.id.imageView0)
        val imageView1 = view.findViewById<ImageView>(R.id.imageView1)
        val imageView2 = view.findViewById<ImageView>(R.id.imageView2)
        val imageView3 = view.findViewById<ImageView>(R.id.imageView3)
        val imageView4 = view.findViewById<ImageView>(R.id.imageView4)
        val imageView5 = view.findViewById<ImageView>(R.id.imageView5)
        val imageView6 = view.findViewById<ImageView>(R.id.imageView6)
        val imageView7 = view.findViewById<ImageView>(R.id.imageView7)

        viewModel.detailUIState.observe(viewLifecycleOwner){
            if(it.frames.isNotEmpty()){
                imageView0.setImageBitmap(it.frames[0])
                imageView1.setImageBitmap(it.frames[1])
                imageView2.setImageBitmap(it.frames[2])
                imageView3.setImageBitmap(it.frames[3])
                imageView4.setImageBitmap(it.frames[4])
                imageView5.setImageBitmap(it.frames[5])
                imageView6.setImageBitmap(it.frames[6])
                imageView7.setImageBitmap(it.frames[7])
            }
        }

        viewModel.detailUIState.observe(viewLifecycleOwner){



        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_screen, container, false)
    }


}