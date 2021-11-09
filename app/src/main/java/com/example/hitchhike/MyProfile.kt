package com.example.hitchhike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MyProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val saveButton = findViewById<Button>(R.id.buttonMyProfileSave)
        val firstName = findViewById<EditText>(R.id.editTextMyProfileFirstName)
        val lastName = findViewById<EditText>(R.id.editTextMyProfileLastName)
        val email = findViewById<EditText>(R.id.editTextMyProfileEmail)
        val phone = findViewById<EditText>(R.id.editTextMyProfilePhone)

        saveButton.setOnClickListener {
            val displayText = firstName.text.toString() + lastName.text.toString() + email.text.toString() + phone.text.toString()
            Toast.makeText(this, displayText, Toast.LENGTH_SHORT).show()
        }

    }
}