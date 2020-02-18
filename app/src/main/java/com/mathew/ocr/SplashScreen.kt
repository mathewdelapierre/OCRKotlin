package com.mathew.ocr

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer

import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//test
        setContentView(R.layout.activity_splash_screen)
        object : CountDownTimer(2000, 1000) {

            /** This method will be invoked on finishing or expiring the timer  */
            override fun onFinish() {
                /** Creates an intent to start new activity  */
                val intent = Intent(baseContext, HomeActivity::class.java)
                //Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                /** Creates a new activity, on finishing this timer  */
                startActivity(intent)

                /** Close this activity screen  */
                finish()
            }

            /** This method will be invoked in every 1000 milli seconds until
             * this timer is expired.Because we specified 1000 as tick time
             * while creating this CountDownTimer
             */
            override fun onTick(millisUntilFinished: Long) {}
        }.start()
    }
}
