package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.hitchhike.R
import com.example.hitchhike.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbtn.setOnClickListener{

            val username = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if(username.isEmpty()){
                binding.editTextEmail.error = "Username Required"
                return@setOnClickListener

            }else if (password.isEmpty()){
                binding.editTextPassword.error = "Password Required"
                return@setOnClickListener

            }else{
                if(username == "admin" && password == "admin"){
                    val intent = Intent(this, MainActivity::class.java).apply {
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onCreateAccountClicked(view: android.view.View) {
        val intent = Intent(this, SignUpActivity::class.java).apply {
        }
        startActivity(intent)
    }
}