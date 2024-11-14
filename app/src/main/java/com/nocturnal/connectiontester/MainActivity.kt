package com.nocturnal.connectiontester

import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nocturnal.connectiontester.services.phoneServices

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        val flashlightSwitch: SwitchCompat = findViewById(R.id.lightSwitch)
        flashlightSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

                startService(Intent(this, phoneServices::class.java))
            } else {

                stopService(Intent(this, phoneServices::class.java))
            }
        }
    }
}