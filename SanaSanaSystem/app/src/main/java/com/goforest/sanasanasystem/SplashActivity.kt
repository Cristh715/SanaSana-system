package com.goforest.sanasanasystem

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("sana_sana_prefs", MODE_PRIVATE)
            val isFirstTime = sharedPref.getBoolean("isFirstTime", true)
            val authToken = sharedPref.getString("auth_token", null)

            if (isFirstTime) {
                startActivity(Intent(this, OnboardingActivity::class.java))
                sharedPref.edit().putBoolean("isFirstTime", false).apply()
            } else if (authToken != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, SPLASH_TIME_OUT)
    }
}