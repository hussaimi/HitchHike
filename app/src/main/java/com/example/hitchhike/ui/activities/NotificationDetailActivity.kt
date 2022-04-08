package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.hitchhike.databinding.ActivityNotificationDetailBinding
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var binding: ActivityNotificationDetailBinding
    private lateinit var scheduleRequestKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Notification Detail"


        var tripId = intent.getStringExtra("tripId")
        var requesterId = intent.getStringExtra("requesterId")
        scheduleRequestKey = intent.getStringExtra("requestKey").toString()
        val status = intent.getStringExtra("status")

        dbReference = Firebase.database.reference

        if (scheduleRequestKey != null) {
            //Toast.makeText(this, scheduleRequestKey, Toast.LENGTH_SHORT).show()
        }

        //fetching trip information from firebase for selected trip
        if (tripId != null) {
            dbReference.child("Trips").child(tripId).get().addOnSuccessListener {
                var tripInfo = it.getValue(TripsInfo::class.java)!!
                if (tripInfo != null) {
                    binding.txtViewFromDetail.text = tripInfo.from
                    binding.txtViewToDetails.text = tripInfo.to
                    binding.txtViewTimeDetail.text = tripInfo.time
                    binding.txtViewDateDetail.text = tripInfo.date
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        //fetching requester information from firebase
        if (requesterId != null) {
            dbReference.child("Users").child(requesterId).get().addOnSuccessListener {
                var user = it.getValue(userInfo::class.java)!!
                if (user != null) {
                    binding.txtViewRequestFromDetail.text = user.fullName
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        if (status == "decline") {
            dbReference.child("ScheduleRequests").child(scheduleRequestKey).child("status")
                .setValue("Complete")
            binding.btnAccept.isVisible = false
            binding.btnDecline.isVisible = false
        }

        binding.btnAccept.setOnClickListener {
            dbReference.child("ScheduleRequests").child(scheduleRequestKey).child("status")
                .setValue("accept")
        }

        binding.btnDecline.setOnClickListener {
            dbReference.child("ScheduleRequests").child(scheduleRequestKey).child("status")
                .setValue("decline")
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}