package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hitchhike.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Login"

        auth = Firebase.auth

        binding.loginbtn.setOnClickListener{

            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if(email.isEmpty()){
                binding.editTextEmail.error = "Username Required"
                return@setOnClickListener

            }
            if (password.isEmpty()){
                binding.editTextPassword.error = "Password Required"
                return@setOnClickListener

            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if(it.isSuccessful){
                        val intent = Intent(this, HomePageActivity::class.java)
                        startActivity(intent)
                    } else{
                        Toast.makeText(this, "Error! " + it.exception?.message.toString() , Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, HomePageActivity::class.java)
            intent.putExtra("userId", currentUser.uid.toString())
            startActivity(intent)
        }
    }

    fun onCreateAccountClicked(view: android.view.View) {
        val intent = Intent(this, SignUpActivity::class.java).apply {
        }
        startActivity(intent)
    }
}