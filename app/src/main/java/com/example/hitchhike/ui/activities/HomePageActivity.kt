package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hitchhike.R
import com.example.hitchhike.databinding.ActivityHomePageBinding

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDriver.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userIs", "Driver")
            startActivity(intent)
        }

        binding.btnRider.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userIs", "Rider")
            startActivity(intent)
        }
    }
}