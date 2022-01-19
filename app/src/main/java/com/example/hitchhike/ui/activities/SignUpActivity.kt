package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hitchhike.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun onAlreadyHaveAccountClicked(view: android.view.View) {
        val intent = Intent(this, LoginActivity::class.java).apply {
        }
        startActivity(intent)
    }
}