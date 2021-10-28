package com.example.hitchhike

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

//    private lateinit var analytics: FirebaseAnalytics
    private val radioButtonDriver: RadioButton by lazy { findViewById(R.id.radioBtnDriver) }
    private val radioButtonRider: RadioButton by lazy { findViewById(R.id.radioBtnRider) }
    private var userType: String? = null
    private val fromLocation: EditText by lazy { findViewById(R.id.editTextFrom) }
    private val toLocation: EditText by lazy { findViewById(R.id.editTextTo) }
    private lateinit var dbReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        analytics = FirebaseAnalytics.getInstance(this)
        val addTripButton = findViewById<Button>(R.id.btnAddTrip)
        addTripButton.setOnClickListener {
            //Toast.makeText(this, "Testing", Toast.LENGTH_SHORT).show()
//            analytics.logEvent("Button_CLicked", null)
            val intent = Intent(this, AddTrip::class.java).apply {
            }
            startActivity(intent)
        }

        dbReference = Firebase.database.reference
        val submitButton = findViewById<Button>(R.id.btnFilter)
        submitButton.setOnClickListener {
//            Toast.makeText(this, dbReference.toString(), Toast.LENGTH_SHORT).show()
//            dbReference.child("Testing").push().setValue(mapOf("First Name" to fromLocation.text.toString())).addOnSuccessListener {
//                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener {
//                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
//            }
        }
    }

    fun onRadioButtonClicked(view: View){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                radioButtonDriver.id ->
                    if (checked) {
                        radioButtonRider.isChecked = false
                        userType = "Driver"
                        Toast.makeText(this,
                            "Selected Item:" + " " +
                                    "" + userType, Toast.LENGTH_SHORT).show()
                    }
                radioButtonRider.id ->
                    if (checked) {
                        radioButtonDriver.isChecked = false
                        userType = "Rider"
                        Toast.makeText(this,
                            "Selected Item:" + " " +
                                    "" + userType, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}