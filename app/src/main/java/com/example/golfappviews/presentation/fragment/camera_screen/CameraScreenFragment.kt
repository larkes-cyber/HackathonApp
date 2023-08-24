package com.example.golfappviews.presentation.fragment.camera_screen

import android.graphics.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.golfappviews.R
import com.example.golfappviews.util.toByteArray
import com.otaliastudios.cameraview.CameraView
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.ByteArrayOutputStream


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
        val stream = view.findViewById<ImageView>(R.id.stream)


        viewModel.streamState.observe(viewLifecycleOwner){
            stream.setImageBitmap(it)
        }

        camera.setLifecycleOwner(this)
        camera.addFrameProcessor { frame ->
            val data = frame.getData<ByteArray>()
            val size = frame.size

            val yuvImage = YuvImage(data, ImageFormat.NV21, size.width, size.height, null)
            val os = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, size.width, size.height), 100, os)
            val jpegByteArray = os.toByteArray()
            var bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.size)
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
            viewModel.onFrameChange(rotatedBitmap.toByteArray())
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

