package com.example.hitchhike

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class login : AppCompatActivity() {

    private lateinit var Username : EditText
    private lateinit var Password : EditText
    private lateinit var loginbtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Username = findViewById(R.id.Username)
        Password = findViewById(R.id.Password)
        loginbtn = findViewById(R.id.loginbtn)

        loginbtn.setOnClickListener{

            val username = Username.text.toString().trim()
            val password = Password.text.toString().trim()

            if(username.isEmpty()){
                Username.error = "Username Required"
                return@setOnClickListener

            }else if (password.isEmpty()){
                Password.error = "Password Required"
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
}