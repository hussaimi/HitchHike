package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.set
import androidx.core.view.isInvisible
import com.example.hitchhike.databinding.ActivityMyProfileBinding
import com.example.hitchhike.model.TripsInfo
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

        var uid = FirebaseAuth.getInstance().uid
        dbReference = Firebase.database.reference

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

        binding.floatingActionButtonMyProfileEdit.setOnClickListener {
            binding.editTextMyProfileFullName.isEnabled = true
            binding.editTextMyProfileFullName.requestFocus()
            binding.editTextMyProfileEmail.isEnabled = true
            binding.editTextMyProfilePhone.isEnabled = true
            binding.buttonMyProfileSave.isInvisible = false
        }

        binding.buttonMyProfileSave.setOnClickListener {

        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}