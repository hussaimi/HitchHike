package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hitchhike.databinding.ActivityNotificationDetailBinding
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var binding: ActivityNotificationDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var tripId = intent.getStringExtra("tripId")
        var requesterId = intent.getStringExtra("requesterId")

        dbReference = Firebase.database.reference
//         fetching trip information from firebase for selected trip
              if (tripId != null) {
                    dbReference.child("Trips").child(tripId).get().addOnSuccessListener {
                        var tripInfo = it.getValue(TripsInfo::class.java)!!
                        if (tripInfo != null) {
                            binding.txtViewFromDetail.text = tripInfo.from
                            binding.txtViewToDetails.text = tripInfo.to
                            binding.txtViewTimeDetail.text = tripInfo.time
                            binding.txtViewDateDetail.text = tripInfo.date
                        }
                    }.addOnFailureListener{
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
                    }.addOnFailureListener{
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}