package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hitchhike.databinding.ActivityNotificationsBinding
import com.example.hitchhike.model.ScheduleRequestInfo
import com.example.hitchhike.ui.adapters.NotificationAdapter

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
        title = "Notifications"

        arrayList = ArrayList()
        scheduleRequestArrayList = ArrayList()
        scheduleRequestKeyArrayList = arrayListOf()

        if (intent.hasExtra("requests")) {
            scheduleRequestArrayList =
                intent.getSerializableExtra("requests") as ArrayList<ScheduleRequestInfo>
            scheduleRequestKeyArrayList =
                intent.getStringArrayListExtra("requestKeys") as ArrayList<String>
            binding.listViewNotification.isClickable = true
            binding.listViewNotification.adapter =
                NotificationAdapter(this, scheduleRequestArrayList)
            binding.listViewNotification.setOnItemClickListener { parent, view, position, l ->
                val requesterId = scheduleRequestArrayList[position].requesterId
                val tripOwnerId = scheduleRequestArrayList[position].tripOwnerId
                val tripId = scheduleRequestArrayList[position].rideId

                // initializing intent to send data to notification detail activity
                val intent = Intent(this, NotificationDetailActivity::class.java)
                intent.putExtra("tripId", tripId)
                intent.putExtra("requesterId", requesterId)
                intent.putExtra("requestKey", scheduleRequestKeyArrayList[position])
                intent.putExtra("status", scheduleRequestArrayList[position].status)
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