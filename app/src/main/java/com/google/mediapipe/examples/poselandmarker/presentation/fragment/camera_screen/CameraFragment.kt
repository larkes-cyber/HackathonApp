/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.poselandmarker.presentation.fragment.camera_screen

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.common.net.MediaType
import com.google.mediapipe.examples.poselandmarker.R
import com.google.mediapipe.examples.poselandmarker.databinding.FragmentCameraBinding
import com.google.mediapipe.examples.poselandmarker.presentation.PoseLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class CameraFragment : Fragment(), PoseLandmarkerHelper.LandmarkerListener, SensorEventListener {

    companion object {
        private const val TAG = "Pose Landmarker"
    }

    enum class MediaType {
        IMAGE,
        VIDEO,
        UNKNOWN
    }
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private var _fragmentCameraBinding: FragmentCameraBinding? = null

    private val fragmentCameraBinding
        get() = _fragmentCameraBinding!!


    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_BACK

    private var corners:LinearLayout? = null

    lateinit var viewModel: CameraViewModel

    private lateinit var backgroundExecutor: ExecutorService

    private var videoMode = false

    private var next = MutableLiveData(false)

    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let { mediaUri ->
                when (val mediaType = loadMediaType(mediaUri)) {
                    MediaType.VIDEO -> {


                    }
                    else -> {}
                }
            }
        }

    private fun getVideoDuration(uri: Uri, context: Context): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)

        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val duration = durationStr?.toLong() ?: 0

        retriever.release()

        return duration
    }


    private fun runDetectionOnVideo(bitmap: Bitmap) {



        backgroundExecutor = Executors.newSingleThreadScheduledExecutor()
        backgroundExecutor.execute {


            poseLandmarkerHelper =
                PoseLandmarkerHelper(
                    context = requireContext(),
                    runningMode = RunningMode.IMAGE,
                    minPoseDetectionConfidence = viewModel.currentMinPoseDetectionConfidence,
                    minPoseTrackingConfidence = viewModel.currentMinPoseTrackingConfidence,
                    minPosePresenceConfidence = viewModel.currentMinPosePresenceConfidence,
                    currentDelegate = viewModel.currentDelegate
                )


            poseLandmarkerHelper.detectImage(bitmap)
                ?.let { resultBundle ->
                    val list = resultBundle.results.first().landmarks()
                    val output = arrayListOf<ArrayList<Float>>()
                    Log.d("erghgffgfrddd", list.toString())
                    if(list.isNotEmpty()) {
                        list[0].forEach {res ->
                            output.add(arrayListOf(res.x(), res.y()))
                        }
                    }
                    viewModel.onCacheCoords(output)
                }
                ?: run { Log.e(TAG, "Error running pose landmarker.") }

            poseLandmarkerHelper.clearPoseLandmarker()
        }
    }
    private fun loadMediaType(uri: Uri): MediaType {
        val mimeType = context?.contentResolver?.getType(uri)
        mimeType?.let {
            if (mimeType.startsWith("image")) return MediaType.IMAGE
            if (mimeType.startsWith("video")) return MediaType.VIDEO
        }

        return MediaType.UNKNOWN
    }

    override fun onResume() {
        super.onResume()

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        backgroundExecutor.execute {
            if(this::poseLandmarkerHelper.isInitialized) {
                if (poseLandmarkerHelper.isClose()) {
                    poseLandmarkerHelper.setupPoseLandmarker()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(this::poseLandmarkerHelper.isInitialized) {
            viewModel.setMinPoseDetectionConfidence(poseLandmarkerHelper.minPoseDetectionConfidence)
            viewModel.setMinPoseTrackingConfidence(poseLandmarkerHelper.minPoseTrackingConfidence)
            viewModel.setMinPosePresenceConfidence(poseLandmarkerHelper.minPosePresenceConfidence)
            viewModel.setDelegate(poseLandmarkerHelper.currentDelegate)
            backgroundExecutor.execute { poseLandmarkerHelper.clearPoseLandmarker() }
        }
        sensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sensorManager =  context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        _fragmentCameraBinding =
            FragmentCameraBinding.inflate(inflater, container, false)


        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this.requireContext()));
        }

        viewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        return fragmentCameraBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        corners = view.findViewById(R.id.focus_back)

        backgroundExecutor = Executors.newSingleThreadExecutor()

        fragmentCameraBinding.viewFinder.post {
            setUpCamera()
        }

        backgroundExecutor.execute {
            poseLandmarkerHelper = PoseLandmarkerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minPoseDetectionConfidence = viewModel.currentMinPoseDetectionConfidence,
                minPoseTrackingConfidence = viewModel.currentMinPoseTrackingConfidence,
                minPosePresenceConfidence = viewModel.currentMinPosePresenceConfidence,
                currentDelegate = viewModel.currentDelegate,
                poseLandmarkerHelperListener = this
            )
        }

        val recButton = view.findViewById<ImageView>(R.id.button_camera)
        val rotateText = view.findViewById<TextView>(R.id.rotate)
        val personText = view.findViewById<TextView>(R.id.person )
        val autoModeButton = view.findViewById<ImageView>(R.id.target)

        val camera = view.findViewById<ImageView>(R.id.camera)


        camera.setOnClickListener {
            viewModel.onVideoMode()
            getContent.launch(arrayOf("image/*", "video/*"))
            poseLandmarkerHelper.clearPoseLandmarker()

        }

        viewModel.frameState.observe(viewLifecycleOwner){state ->
            rotateText.text = state.rotate
            personText.text = if(state.checkPerson) "Человек есть" else "Нет человека"
            if(state.hitStarted) {
                recButton.setColorFilter(context!!.resources.getColor(R.color.red))
            }
            if(state.hitEnded) {
                recButton.setColorFilter(context!!.resources.getColor(R.color.red))
                view.findNavController().navigate(R.id.action_camera_fragment_to_detailScreenFragment)
            }
            if(state.videoMode){
                videoMode = true
            }

        }



        autoModeButton.setOnClickListener {
            viewModel.onAutoChange()
        }


        recButton.setOnClickListener {
            viewModel.switchRecording()
        }

    }


    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindCameraUseCases() {

        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .build()

        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        val bitmapBuffer =
                            Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )

                        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
                        image.close()

                        val matrix = Matrix().apply {
                            // Rotate the frame received from the camera to be in the same direction as it'll be shown
                            postRotate(image.imageInfo.rotationDegrees.toFloat())

                        }
                        val rotatedBitmap = Bitmap.createBitmap(
                            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
                            matrix, true
                        )
                        if(!videoMode) {
                            viewModel.onCurrentFrameChange(rotatedBitmap)
                            detectPose(rotatedBitmap)
                        }
                    }
                }

        cameraProvider.unbindAll()

        try {

            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) { }
    }

    private fun detectPose(imageProxy: Bitmap) {
        if(this::poseLandmarkerHelper.isInitialized) {
            poseLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            fragmentCameraBinding.viewFinder.display.rotation
    }

    override fun onResults(
        resultBundle: PoseLandmarkerHelper.ResultBundle,
    ) {
        val list = resultBundle.results.first().landmarks()
        val output = arrayListOf<ArrayList<Float>>()
        if(list.isNotEmpty()) {
            list[0].forEach {res ->
                output.add(arrayListOf(res.x(), res.y()))
            }
        }
        viewModel.onFrameStream(output)
        activity?.runOnUiThread {
            if (_fragmentCameraBinding != null) {
                fragmentCameraBinding.overlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                fragmentCameraBinding.overlay.invalidate()
            }
        }
    }

    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            if (errorCode == PoseLandmarkerHelper.GPU_ERROR) {

            }
        }
    }
    private var gravityValues: FloatArray? = null
    private var magneticValues: FloatArray? = null

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            gravityValues = event.values.clone()
        }
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = event.values.clone()
        }


        gravityValues?.let { gravity ->
            magneticValues?.let { geomagnetic ->
                val rotationMatrix = FloatArray(9)
                val inclinationMatrix = FloatArray(9)
                val remappedMatrix = FloatArray(9)

                if (SensorManager.getRotationMatrix(
                        rotationMatrix,
                        inclinationMatrix,
                        gravity,
                        geomagnetic
                    )
                ) {
                    SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedMatrix
                    )

                    val orientationValues = FloatArray(3)
                    SensorManager.getOrientation(remappedMatrix, orientationValues)

                    // orientationValues[1] contains the pitch value in radians.
                    // Convert it to degrees.
                    val pitchInDegrees = Math.toDegrees(orientationValues[1].toDouble()).toFloat()

                    // Check if the pitch angle is close to 0 degrees (vertical).
                    val isVertical = Math.abs(pitchInDegrees) < 10.0

                    if(corners != null) {
                        if (isVertical) {
                            corners!!.setBackgroundResource(R.drawable.focus_back_green)
                        }else{
                            corners!!.setBackgroundResource(R.drawable.focus_back)
                        }
                    }
                    Log.d("werfdfddgferfg",isVertical.toString())

                    // Now you can use the 'isVertical' value to determine if the device is in a vertical state.
                    // Update your UI or perform actions accordingly.
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


}

fun getVideoPathFromMediaStore(uri: Uri, context:Context): String? {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
    val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
    cursor?.moveToFirst()
    val path = cursor?.getString(columnIndex ?: 0)
    cursor?.close()
    return path
}