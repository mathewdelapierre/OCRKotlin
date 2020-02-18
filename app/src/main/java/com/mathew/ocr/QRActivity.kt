package com.mathew.ocr

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

import java.util.Locale

class QRActivity : AppCompatActivity(), OnClickListener {
    private var scanBtn: Button? = null
    private var tvScanFormat: TextView? = null
    private var tvScanContent: TextView? = null
    private var llSearch: LinearLayout? = null
    private var textToSpeech: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        scanBtn = findViewById<View>(R.id.scan_button) as Button
        tvScanFormat = findViewById<View>(R.id.tvScanFormat) as TextView
        tvScanContent = findViewById<View>(R.id.tvScanContent) as TextView
        llSearch = findViewById<View>(R.id.llSearch) as LinearLayout

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

        llSearch!!.visibility = View.GONE
        val integrator = IntentIntegrator(this)
        //integrator.setPrompt("Scan a barcode or QRcode");
        //integrator.setOrientationLocked(false);
        integrator.initiateScan()
        scanBtn!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        llSearch!!.visibility = View.GONE
        val integrator = IntentIntegrator(this)
        //integrator.setPrompt("Scan a barcode or QRcode");
        //integrator.setOrientationLocked(false);
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                llSearch!!.visibility = View.GONE
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                llSearch!!.visibility = View.VISIBLE
                tvScanContent!!.text = result.contents
                tvScanFormat!!.text = result.formatName
                val speechStatus = textToSpeech!!.speak(
                    tvScanContent!!.text.toString(),
                    TextToSpeech.QUEUE_FLUSH,
                    null
                )
                if (speechStatus == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!")
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {

        private val TAG = "OCRActivity"
        private val requestPermissionID = 101
    }
}