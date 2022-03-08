package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hitchhike.R
import com.example.hitchhike.databinding.ActivityMyRidesDetailBinding

class MyRidesDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRidesDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRidesDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.personDetail.txtViewEmail.text = "hello"
    }
}