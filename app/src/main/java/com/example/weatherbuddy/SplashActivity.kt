package com.example.weatherbuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            checkPreferencesAndProceed()
        }, 3000)

    }

    private fun checkPreferencesAndProceed() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val isItem1Enabled = sharedPreferences.getBoolean("enabled", false)

        if (isItem1Enabled) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val bottomSheet = CityBottomSheet()
            bottomSheet.show(supportFragmentManager, "CityNameBottomSheet")
        }
    }
}