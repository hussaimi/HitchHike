package com.example.hitchhike

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AddTrip : AppCompatActivity() {


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)
        title = "Add Trip"

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
//                    Toast.makeText(this@AddTrip,
//                        "Selected Item:" + " " +
//                                "" + numberArray[position], Toast.LENGTH_SHORT).show()
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
                this@AddTrip,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        mTimePicker = TimePickerDialog(this,
            { _, hourOfDay, minute -> textViewTime.text = String.format("%02d : %02d", hourOfDay, minute) }, hour, minute, true)

        textViewTime.setOnClickListener {
            mTimePicker.show()
        }

        //getting firebase database reference
        dbReference = Firebase.database.reference

        saveButton.setOnClickListener {
            writeNewTrip()
            val intent = Intent(this@AddTrip, MainActivity::class.java).apply {}
            startActivity(intent)
            //Toast.makeText(this, dbReference.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textViewDate.text = sdf.format(cal.time)
    }

    private fun writeNewTrip(){
        val fLocation = fromLocation.text.toString()
        val tLocation = toLocation.text.toString()
        val desc = description.text.toString()
        val date = textViewDate.text.toString()
        val time = textViewTime.text.toString()
        val trip = TripsInfo(fLocation, tLocation, desc, date, time, noOfPeople, userType)

        dbReference.child("Trips")
            .push()
            .setValue(trip)
            .addOnSuccessListener { Toast.makeText(this@AddTrip, "Trip Added Successfully", Toast.LENGTH_LONG).show() }
            .addOnFailureListener { Toast.makeText(this@AddTrip, it.message.toString(), Toast.LENGTH_SHORT).show() }
        //Toast.makeText(this, databaseReference.child("Trips").push().setValue(trip).isSuccessful.toString(), Toast.LENGTH_SHORT).show()
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
//                        Toast.makeText(this@AddTrip,
//                            "Selected Item:" + " " +
//                                    "" + userType, Toast.LENGTH_SHORT).show()
                    }
                radioButtonRider.id ->
                    if (checked) {
                        radioButtonDriver.isChecked = false
                        userType = "Rider"
//                        Toast.makeText(this@AddTrip,
//                            "Selected Item:" + " " +
//                                    "" + userType, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

}


//----------------------------- Extra ------------------------------------

//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // inside the method of on Data change we are setting
//                // our object class to our database reference.
//                // data base reference will sends data to firebase.
//                database.setValue(trip).isSuccessful
//
//                // after adding this data we are showing toast message.
//                Toast.makeText(this@AddTrip, database.setValue(trip).isSuccessful.toString(), Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // if the data is not added or it is cancelled then
//                // we are displaying a failure toast message.
//                Toast.makeText(this@AddTrip, "Fail to add data $error", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        })
//Toast.makeText(this, databaseReference.child("Trips").setValue(trip).toString(), Toast.LENGTH_SHORT).show()