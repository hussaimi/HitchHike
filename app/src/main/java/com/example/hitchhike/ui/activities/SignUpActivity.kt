package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.hitchhike.databinding.ActivitySignUpBinding
import com.example.hitchhike.model.userInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Sign up"

        dbReference = Firebase.database.reference

        auth = Firebase.auth
        binding.btnSignUpSubmit.setOnClickListener {
            if(TextUtils.isEmpty(binding.editTextFullName.text.toString().trim())){
                binding.editTextFullName.error = "Name is Required"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(binding.editTextUserId.text.toString().trim())){
                binding.editTextUserId.error = "Username is Required"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(binding.editTextEmail.text.toString().trim())){
                binding.editTextEmail.error = "Email is Required"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(binding.editTextPassword.text.toString().trim())){
                binding.editTextPassword.error = "Password is Required"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(binding.editTextPhone.text.toString().trim())){
                binding.editTextPhone.error = "Phone Number is Required"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(binding.editTextEmail.text.toString().trim(), binding.editTextPassword.text.toString().trim())
                .addOnCompleteListener(this){
                    if(it.isSuccessful){
                        //Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        storeUser(user)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("userId", user?.uid.toString())
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error! " + it.exception?.message.toString() , Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            Toast.makeText(this, currentUser.email, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userId", currentUser.uid.toString())
            startActivity(intent)
        }
    }

    private fun storeUser(user: FirebaseUser?) {
        val fullName = binding.editTextFullName.text.toString()
        val email = user?.email
        val phoneNumber = binding.editTextPhone.text.toString()
        val userId = user?.uid
        val userAccount = userInfo(fullName, email, phoneNumber, userId)
        if (userId != null) {
            dbReference.child("Users")
                .child(userId)
                .setValue(userAccount)
                .addOnSuccessListener { Toast.makeText(this, "User Added Successfully", Toast.LENGTH_LONG).show() }
                .addOnFailureListener { Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show() }
        }
    }

    fun onAlreadyHaveAccountClicked(view: android.view.View) {
        val intent = Intent(this, LoginActivity::class.java).apply {
        }
        startActivity(intent)
    }
}
