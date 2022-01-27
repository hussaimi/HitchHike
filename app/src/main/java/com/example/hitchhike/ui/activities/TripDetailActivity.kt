package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.hitchhike.R
import com.example.hitchhike.model.ScheduleRequestInfo
import com.example.hitchhike.model.TripsInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TripDetailActivity : AppCompatActivity() {

    private val fromDetails: TextView by lazy { findViewById(R.id.txtViewFromDetail) }
    private val toDetails: TextView by lazy { findViewById(R.id.txtViewToDetails) }
    private val descDetails: TextView by lazy { findViewById(R.id.txtViewDescDetail) }
    private val dateDetails: TextView by lazy { findViewById(R.id.txtViewDateDetail) }
    private val timeDetails: TextView by lazy { findViewById(R.id.txtViewTimeDetail) }
    private val noOfPeopleDetails: TextView by lazy { findViewById(R.id.txtViewNoOfPeopleDetail) }
    private val lookingForDetails: TextView by lazy { findViewById(R.id.txtViewLookinForDetail) }
    private val btnSchedule: Button by lazy { findViewById(R.id.btnSchedule) }
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Trip Details"

        if(intent.hasExtra("ownerOfTrip")){
            if(intent.getStringExtra("ownerOfTrip") == "true"){
                btnSchedule.isEnabled = false
            }
        }

        val trip = intent.getSerializableExtra("TripInfo") as? TripsInfo
        val myId = FirebaseAuth.getInstance().uid.toString()
        val tripOwnerId = intent.getStringExtra("TripOwnerId")
        val tripId = intent.getStringExtra("TripId")

        if (trip != null) {
            fromDetails.text = trip.from
            toDetails.text = trip.to
            descDetails.text = trip.desc
            dateDetails.text = trip.date
            timeDetails.text = trip.time
            noOfPeopleDetails.text = trip.noOfPeople
            lookingForDetails.text = trip.userType

            Toast.makeText(this, myId + " " +tripOwnerId + " " + tripId, Toast.LENGTH_LONG).show()
        }

        dbReference = Firebase.database.reference
        btnSchedule.setOnClickListener {
            //check if the request already exist in database using dataSnapshot.
            val status = "pending"
            val scheduleRequest = ScheduleRequestInfo(myId, tripOwnerId, tripId, status)
            dbReference.child("ScheduleRequests")
                .push()
                .setValue(scheduleRequest)
                .addOnSuccessListener { Toast.makeText(this, "Request Send", Toast.LENGTH_LONG).show() }
                .addOnFailureListener { Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}