package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isInvisible
import com.example.hitchhike.databinding.ActivityMyProfileBinding
import com.example.hitchhike.model.userInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    private lateinit var dbReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "My Profile"

        binding.editTextMyProfileFullName.isEnabled = false
        binding.editTextMyProfileEmail.isEnabled = false
        binding.editTextMyProfilePhone.isEnabled = false
        binding.buttonMyProfileSave.isInvisible = true

        var uid = FirebaseAuth.getInstance().uid.toString()
        dbReference = Firebase.database.reference

        updateProfile(uid)

        binding.floatingActionButtonMyProfileEdit.setOnClickListener {
            binding.editTextMyProfileFullName.isEnabled = true
            binding.editTextMyProfileFullName.requestFocus()
            binding.editTextMyProfileEmail.isEnabled = true
            binding.editTextMyProfilePhone.isEnabled = true
            binding.buttonMyProfileSave.isInvisible = false
        }

        binding.buttonMyProfileSave.setOnClickListener {

            //initializing variables to store the values in the editText fields
            val fullName = binding.editTextMyProfileFullName.text.toString()
            val email = binding.editTextMyProfileEmail.text.toString()
            val phone = binding.editTextMyProfilePhone.text.toString()
            val user = userInfo(fullName, email, phone, uid)

            //validating data
            if (fullName.isEmpty()) {
                binding.editTextMyProfileFullName.error = "Full name is required."
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.editTextMyProfileEmail.error = "Email is required."
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                binding.editTextMyProfilePhone.error = "Phone number is required."
                return@setOnClickListener
            }

            //updating user email for login credentials
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser!!.updateEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@MyProfileActivity,
                        "Login Email Updated",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@MyProfileActivity,
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            //extracting data to Firebase
            dbReference.child("Users")
                .child(uid)
                .setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@MyProfileActivity,
                        "User Information Updated",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@MyProfileActivity,
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            updateProfile(uid)
            //reverting to before Edit Profile button is clicked; fields are not editable and Save button is invisible
            binding.editTextMyProfileFullName.isEnabled = false
            binding.editTextMyProfileEmail.isEnabled = false
            binding.editTextMyProfilePhone.isEnabled = false
            binding.buttonMyProfileSave.isInvisible = true

        }

    }

    fun updateProfile(uid: String) {
        if (uid != null) {
            dbReference.child("Users").child(uid).get().addOnSuccessListener {
                var user = it.getValue(userInfo::class.java)!!
                if (user != null) {
                    binding.editTextMyProfileFullName.setText(user.fullName)
                    binding.editTextMyProfileEmail.setText(user.email)
                    binding.editTextMyProfilePhone.setText(user.phoneNumber)
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}