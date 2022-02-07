package com.example.hitchhike.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.hitchhike.R
import com.example.hitchhike.model.TripsInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class AddTripActivity : AppCompatActivity() {


    val cal: Calendar by lazy { Calendar.getInstance() }
    private val fromLocation: EditText by lazy { findViewById(R.id.editTextFrom) }
    private val toLocation: EditText by lazy { findViewById(R.id.editTextTo) }
    private val description: EditText by lazy { findViewById(R.id.editTextDescription) }
    private val textViewDate: TextView by lazy { findViewById(R.id.textViewDate) }
    private val textViewTime: TextView by lazy { findViewById(R.id.textViewTime) }
    private val radioButtonDriver: RadioButton by lazy { findViewById(R.id.radioBtnDriver) }
    private val radioButtonRider: RadioButton by lazy { findViewById(R.id.radioBtnRider) }
    private var noOfPeople: String? = null
    private var userType: String? = null
    private val saveButton: Button by lazy { findViewById(R.id.btnSubmit) }
    private lateinit var dbReference: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Add Trip"

        Toast.makeText(this, FirebaseAuth.getInstance().uid.toString(), Toast.LENGTH_SHORT).show()

        val mTimePicker: TimePickerDialog
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val spinner = findViewById<Spinner>(R.id.spinnerNoOfPeople)

        val numberArray = resources.getStringArray(R.array.numberOfPeopleArray)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, numberArray)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    noOfPeople =  numberArray[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        textViewDate.setOnClickListener {
            DatePickerDialog(
                this@AddTripActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        mTimePicker = TimePickerDialog(this,
            { _, hourOfDay, minute -> textViewTime.text = updateTime(hourOfDay, minute) }, hour, minute, false)

        textViewTime.setOnClickListener {
            mTimePicker.show()
        }

        //getting firebase database reference
        dbReference = Firebase.database.reference

        saveButton.setOnClickListener {
            writeNewTrip()
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textViewDate.text = sdf.format(cal.time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTime(hourOfDay: Int, minutes: Int): String {
        //16:20:00 AM
        var time = LocalTime.of(hourOfDay, minutes)
        return time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
    }

    private fun writeNewTrip(){
        val fLocation = fromLocation.text.toString()
        val tLocation = toLocation.text.toString()
        val desc = description.text.toString()
        val date = textViewDate.text.toString()
        val time = textViewTime.text.toString()
        val trip = TripsInfo(fLocation, tLocation, desc, date, time, noOfPeople, userType, FirebaseAuth.getInstance().uid.toString())

        dbReference.child("Trips")
            .push()
            .setValue(trip)
            .addOnSuccessListener { Toast.makeText(this@AddTripActivity, "Trip Added Successfully", Toast.LENGTH_LONG).show() }
            .addOnFailureListener { Toast.makeText(this@AddTripActivity, it.message.toString(), Toast.LENGTH_SHORT).show() }
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
                    }
                radioButtonRider.id ->
                    if (checked) {
                        radioButtonDriver.isChecked = false
                        userType = "Rider"
                    }
            }
        }
    }

}