package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hitchhike.R
import com.example.hitchhike.databinding.ActivityNotificationsBinding
import com.example.hitchhike.model.NotificationsInfo
import com.example.hitchhike.ui.adapters.NotificationAdapter

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var arrayList: ArrayList<NotificationsInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        arrayList = ArrayList()

        val senderId = arrayOf("hussaimi", "mohammad")

        val receiverId = arrayOf("iftikhta", "moiz")

        val rideId = arrayOf("ride1", "ride2")

        for(i in senderId.indices){
            val notification = NotificationsInfo(senderId[i], receiverId[i], rideId[i])
            arrayList.add(notification)
        }

        binding.listViewNotification.isClickable = true
        binding.listViewNotification.adapter = NotificationAdapter(this, arrayList)
        binding.listViewNotification.setOnItemClickListener { parent, view, position, l ->

            val senderId = senderId[position]
            val receiverId = receiverId[position]
            val rideId = rideId[position]

            Toast.makeText(this, senderId + ", " + receiverId + ", " + rideId, Toast.LENGTH_SHORT).show()

//            create intent object to send data to next activity
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}