package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hitchhike.databinding.ActivityTripDetailsBinding
import com.example.hitchhike.model.ScheduleRequestInfo
import com.example.hitchhike.model.TripsInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TripDetailActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var scheduleDbReference: DatabaseReference
    private lateinit var binding: ActivityTripDetailsBinding
    private lateinit var tripOwnerId: String
    private lateinit var myId: String
    private lateinit var tripId: String
    private var rideExist = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Trip Details"

        if (intent.hasExtra("ownerOfTrip")) {
            if (intent.getStringExtra("ownerOfTrip") == "true") {
                binding.btnSchedule.isEnabled = false
            }
        }

        val trip = intent.getSerializableExtra("TripInfo") as? TripsInfo
        myId = FirebaseAuth.getInstance().uid.toString()
        tripOwnerId = intent.getStringExtra("TripOwnerId").toString()
        tripId = intent.getStringExtra("TripId").toString()

        if (trip != null) {
            binding.txtViewFromDetail.text = trip.from
            binding.txtViewToDetails.text = trip.to
            binding.txtViewDescDetail.text = trip.desc
            binding.txtViewDateDetail.text = trip.date
            binding.txtViewTimeDetail.text = trip.time
//            binding.txtViewNoOfPeopleDetail.text = trip.noOfPeople
            if (trip.lookingFor.equals("Rider")) {
                binding.txtViewLookinForDetail.text = "Rider"
            } else if (trip.lookingFor.equals("Driver")) {
                binding.txtViewLookinForDetail.text = "Driver"
            }

        }

        dbReference = Firebase.database.reference
        scheduleDbReference = FirebaseDatabase.getInstance().getReference("ScheduleRequests")

        //check if the request already exist in database using dataSnapshot.
        //checkRequestExistence()

        binding.btnSchedule.setOnClickListener {
            val status = "pending"
            val scheduleRequest = ScheduleRequestInfo(myId, tripOwnerId, tripId, status)
            dbReference.child("ScheduleRequests")
                .push()
                .setValue(scheduleRequest)
                .addOnSuccessListener {
                    Toast.makeText(this, "Request Send", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun checkRequestExistence() {
        scheduleDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (scheduleSnapshot in snapshot.children) {
                        val request = scheduleSnapshot.getValue(ScheduleRequestInfo::class.java)
                        if (request != null) {
                            if (request.requesterId == myId && request.tripOwnerId == tripOwnerId && request.rideId == tripId && request.status == "pending") {
                                rideExist = 0
                                binding.btnSchedule.isEnabled = false
                                binding.btnSchedule.text = "Request Exist"
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}