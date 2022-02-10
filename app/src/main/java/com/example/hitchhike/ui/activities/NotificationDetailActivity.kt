package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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


        var tripId = intent.getStringExtra("tripId")
        var requesterId = intent.getStringExtra("requesterId")
        scheduleRequestKey = intent.getStringExtra("requestKey").toString()

        dbReference = Firebase.database.reference

        if (scheduleRequestKey != null){
            Toast.makeText(this, scheduleRequestKey, Toast.LENGTH_SHORT).show()
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

        binding.btnAccept.setOnClickListener {
            dbReference.child("ScheduleRequests").child(scheduleRequestKey).child("status").setValue("accept")
        }

        binding.btnDecline.setOnClickListener {
            dbReference.child("ScheduleRequests").child(scheduleRequestKey).child("status").setValue("decline")
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

//select * from scheduleRequest where rideId = ________
//dbReference = FirebaseDatabase.getInstance().getReference("ScheduleRequests")
//val query = dbReference.orderByChild("rideId").equalTo("-MttG_j0SaRh-0wK5Cm1")
//query.addListenerForSingleValueEvent(object: ValueEventListener {
//    override fun onCancelled(dataSnapshot: DatabaseError) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onDataChange(dataSnapshot: DataSnapshot) {
//        dataSnapshot.children.forEach { childSnapshot->
//            Toast.makeText(this@NotificationDetailActivity, childSnapshot.key, Toast.LENGTH_SHORT).show()
//            //val textValue = childSnapshot.child("text").getValue(String::class.java)
//        }
//    }
//})