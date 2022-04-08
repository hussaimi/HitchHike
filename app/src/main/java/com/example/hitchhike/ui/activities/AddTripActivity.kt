package com.example.hitchhike.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.hitchhike.databinding.ActivityAddTripBinding
import com.example.hitchhike.model.TripsInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class AddTripActivity : AppCompatActivity() {

    private val cal: Calendar by lazy { Calendar.getInstance() }
    private val radioButtonDriver: RadioButton by lazy { findViewById(R.id.radioBtnDriver) }
    private val radioButtonRider: RadioButton by lazy { findViewById(R.id.radioBtnRider) }
    private var lookingFor: String? = null
    private lateinit var dbReference: DatabaseReference
    private var isDateSelected: Boolean = false
    private var isTimeSelected: Boolean = false
    private lateinit var binding: ActivityAddTripBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Add Trip"

        val mTimePicker: TimePickerDialog
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        binding.textViewDate.setOnClickListener {
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
            { _, hourOfDay, minute -> binding.textViewTime.text = updateTime(hourOfDay, minute) },
            hour,
            minute,
            false
        )

        binding.textViewTime.setOnClickListener {
            mTimePicker.show()
        }

        //getting firebase database reference
        dbReference = Firebase.database.reference

        binding.btnSubmit.setOnClickListener {
            binding.textViewDate.error = null
            binding.textViewTime.error = null
            binding.radioBtnDriver.error = null
            when{
                binding.editTextFrom.text.isEmpty() ->{
                    binding.editTextFrom.error = "Required"
                    return@setOnClickListener
                }
                binding.editTextTo.text.isEmpty() -> {
                    binding.editTextTo.error = "Required"
                    return@setOnClickListener
                }
                binding.editTextDescription.text.isEmpty() -> {
                    binding.editTextDescription.error = "Required"
                    return@setOnClickListener
                }
                !isDateSelected -> {
                    binding.textViewDate.error ="Required"
                    return@setOnClickListener
                }
                !isTimeSelected -> {
                    binding.textViewTime.error = "Required"
                    return@setOnClickListener
                }
                (!binding.radioBtnDriver.isChecked && !binding.radioBtnRider.isChecked) -> {
                    binding.radioBtnDriver.error = "Required"
                    return@setOnClickListener
                }
                else -> {
                    writeNewTrip()
                    onBackPressed()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        isDateSelected = true
        binding.textViewDate.text = sdf.format(cal.time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTime(hourOfDay: Int, minutes: Int): String {
        //16:20:00 AM
        var time = LocalTime.of(hourOfDay, minutes)
        isTimeSelected = true
        return time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun writeNewTrip() {
        val fLocation = binding.editTextFrom.text.toString()
        val tLocation = binding.editTextTo.text.toString()
        val desc = binding.editTextDescription.text.toString()
        val date = binding.textViewDate.text.toString()
        val time = binding.textViewTime.text.toString()
        val trip = TripsInfo(
            fLocation,
            tLocation,
            desc,
            date,
            time,
            1.toString(),
            lookingFor,
            FirebaseAuth.getInstance().uid.toString()
        )

        dbReference.child("Trips")
            .push()
            .setValue(trip)
            .addOnSuccessListener {
                Toast.makeText(
                    this@AddTripActivity,
                    "Trip Added Successfully",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@AddTripActivity,
                    it.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                radioButtonDriver.id ->
                    if (checked) {
                        radioButtonRider.isChecked = false
                        lookingFor = "Driver"
                    }
                radioButtonRider.id ->
                    if (checked) {
                        radioButtonDriver.isChecked = false
                        lookingFor = "Rider"
                    }
            }
        }
    }

}