package com.example.golfappviews.presentation.fragment.camera_screen

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.golfappviews.R
import com.example.golfappviews.util.Constants.APP_NAME
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Mode
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File


@AndroidEntryPoint
class CameraScreenFragment : Fragment() {

    private lateinit var viewModel:CameraScreenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this.requireContext()));
        }

        viewModel = ViewModelProvider(this).get(CameraScreenViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val camera = view.findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.mode = Mode.VIDEO

        val stream = view.findViewById<ImageView>(R.id.stream)
        val takePhotoButton = view.findViewById<ImageView>(R.id.button_camera)
        val mediaDir = File(context!!.getExternalFilesDir(null), "video.mp4")



        fun switchVideoMode(bool:Boolean){
            if(bool){
                camera.mode = Mode.VIDEO
                stream.visibility = View.GONE
            }else{
                stream.visibility = View.VISIBLE
            }
        }

        viewModel.streamImage.observe(viewLifecycleOwner){frame ->
            stream.setImageBitmap(frame)
        }
        viewModel.videoStatus.observe(viewLifecycleOwner){status ->
            if(status != null) {
                switchVideoMode(status)
                if (status) {
                    camera.takeVideo(mediaDir!!)
                    takePhotoButton.setColorFilter(context!!.resources.getColor(R.color.red))
                } else {
                    takePhotoButton.setColorFilter(getContext()!!.resources.getColor(R.color.white))
                    camera.stopVideo()
                    view.findNavController()
                        .navigate(R.id.action_cameraScreenFragment_to_detailScreenFragment)
                }
            }
        }

        camera.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                Log.d("edfgfdsdddffdfgf",result.file.isFile.toString())
            }

            override fun onVideoRecordingEnd() {
                super.onVideoRecordingEnd()
                Log.d("edfgfdsdddffdfgf","result.file.isFile.toString()")
            }

        })

        camera.addFrameProcessor { frame ->
            Log.d("wefgfdsdfgb","#########")
            val size = frame.size
            val data = frame.getData<ByteArray>()
            val yuvImage = YuvImage(data, ImageFormat.NV21, size.width, size.height, null)
            val os = ByteArrayOutputStream()
            val img = yuvImage.compressToJpeg(Rect(0, 0, size.width, size.height), 100, os)
            val jpegByteArray = os.toByteArray()

            viewModel.onFrameChange(jpegByteArray)
        }

        takePhotoButton.setOnClickListener {
            viewModel.switchVideoStatus()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_screen, container, false)
    }
}


