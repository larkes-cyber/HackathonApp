package com.example.golfappviews.presentation.fragment.detail_screen

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.example.golfappviews.R
import java.io.File


class DetailScreenFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val videoPlayer = view.findViewById<VideoView>(R.id.videoView)
        val mediaController = MediaController(context)
        videoPlayer.setMediaController(mediaController)

        val file = Uri.fromFile(File(context!!.getExternalFilesDir(null), "video.mp4"))
        videoPlayer.setVideoURI(file)
        videoPlayer.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_screen, container, false)
    }


}