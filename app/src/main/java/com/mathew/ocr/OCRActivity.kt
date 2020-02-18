package com.mathew.ocr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer

import java.io.IOException
import java.util.Locale

class OCRActivity : AppCompatActivity() {

    internal lateinit var mCameraView: SurfaceView

    internal lateinit var mTextView: TextView
    internal lateinit var mCameraSource: CameraSource
    private var textToSpeech: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        mCameraView = findViewById(R.id.surfaceView)
        mTextView = findViewById(R.id.text_view)


        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ttsLang = textToSpeech!!.setLanguage(Locale.US)

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language is not supported!")
                } else {
                    Log.i("TTS", "Language Supported.")
                }
                Log.i("TTS", "Initialization success.")
            } else {
                Toast.makeText(applicationContext, "TTS Initialization failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        startCameraSource()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: $requestCode")
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mCameraSource.start(mCameraView.holder)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun startCameraSource() {

        //Create the TextRecognizer
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()

        if (!textRecognizer.isOperational) {
            Log.w(TAG, "Detector dependencies not loaded yet")
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {

                            ActivityCompat.requestPermissions(
                                this@OCRActivity,
                                arrayOf(Manifest.permission.CAMERA),
                                requestPermissionID
                            )
                            return
                        }
                        mCameraSource.start(mCameraView.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    mCameraSource.stop()
                }
            })

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {}

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 */
                override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                    val items = detections.detectedItems
                    if (items.size() != 0) {

                        mTextView.post {
                            val stringBuilder = StringBuilder()



                            for (i in 0 until items.size()) {
                                val item = items.valueAt(i)
                                stringBuilder.append(item.value)
                                stringBuilder.append("\n")
                            }
                            mTextView.text = stringBuilder.toString()

                            val speechStatus = textToSpeech!!.speak(
                                mTextView.text.toString(),
                                TextToSpeech.QUEUE_FLUSH,
                                null
                            )

                            if (speechStatus == TextToSpeech.ERROR) {
                                Log.e("TTS", "Error in converting Text to Speech!")
                            }
                        }
                    }
                }
            })
        }
    }

    companion object {

        private val TAG = "OCRActivity"
        private val requestPermissionID = 101
    }
}
