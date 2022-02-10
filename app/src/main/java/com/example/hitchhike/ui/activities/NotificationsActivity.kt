package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hitchhike.databinding.ActivityNotificationsBinding
import com.example.hitchhike.model.ScheduleRequestInfo
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo
import com.example.hitchhike.ui.adapters.NotificationAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/*Things to do
*   1. Get requesterId, tripOwnerId, and tripId
*   2. Extract data of requester using datasnapshot using requesterID, and same for tripOwner and Trip as well.
*   3. Show username instead of requesterId in notification menu
*   4. Details page, show trip info, requester info, and on bottom, accept and decline button.
**/

class NotificationsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var arrayList: ArrayList<ScheduleRequestInfo>
    private lateinit var scheduleRequestArrayList: ArrayList<ScheduleRequestInfo>
    private lateinit var scheduleRequestKeyArrayList: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        arrayList = ArrayList()
        scheduleRequestArrayList = ArrayList()
        scheduleRequestKeyArrayList = arrayListOf()

        if (intent.hasExtra("requests")) {
            scheduleRequestArrayList = intent.getSerializableExtra("requests") as ArrayList<ScheduleRequestInfo>
            scheduleRequestKeyArrayList = intent.getStringArrayListExtra("requestKeys") as ArrayList<String>
            binding.listViewNotification.isClickable = true
            binding.listViewNotification.adapter = NotificationAdapter(this, scheduleRequestArrayList)
            binding.listViewNotification.setOnItemClickListener { parent, view, position, l ->
                val requesterId = scheduleRequestArrayList[position].requesterId
                val tripOwnerId = scheduleRequestArrayList[position].tripOwnerId
                val tripId = scheduleRequestArrayList[position].rideId

                // initializing intent to send data to notification detail activity
                val intent = Intent(this, NotificationDetailActivity::class.java)
                intent.putExtra("tripId", tripId)
                intent.putExtra("requesterId", requesterId)
                intent.putExtra("requestKey", scheduleRequestKeyArrayList[position])
                startActivity(intent)

            }
        } else {
            arrayList.add(ScheduleRequestInfo(null, null, null, null))
            binding.listViewNotification.adapter = NotificationAdapter(this, arrayList)
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}



//        arrayList = ArrayList()
//
//        val senderId = arrayOf("hussaimi", "mohammad")
//
//        val receiverId = arrayOf("iftikhta", "moiz")
//
//        val rideId = arrayOf("ride1", "ride2")
//
//        for(i in senderId.indices){
//            val notification = NotificationsInfo(senderId[i], receiverId[i], rideId[i])
//            arrayList.add(notification)
//        }

//        binding.listViewNotification.isClickable = true
//        binding.listViewNotification.adapter = NotificationAdapter(this, arrayList)
//        binding.listViewNotification.setOnItemClickListener { parent, view, position, l ->
//            val senderId = senderId[position]
//            val receiverId = receiverId[position]
//            val rideId = rideId[position]
//
//            Toast.makeText(this, senderId + ", " + receiverId + ", " + rideId, Toast.LENGTH_SHORT).show()
//
////            create intent object to send data to next activity
//        }