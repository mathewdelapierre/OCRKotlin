package com.mathew.ocr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val  btn_scan = findViewById<View>(R.id.btn_scan) as Button
        val btn_ocr = findViewById<View>(R.id.btn_ocr) as Button

        btn_scan.setOnClickListener {
            val i = Intent(this@HomeActivity, QRActivity::class.java)
            startActivity(i)
        }

        btn_ocr.setOnClickListener {
            val i = Intent(this@HomeActivity, OCRActivity::class.java)
            startActivity(i)
        }
    }
}
