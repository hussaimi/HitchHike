package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hitchhike.R
import com.example.hitchhike.databinding.ActivityMyRidesDetailBinding
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo

class MyRidesDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRidesDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRidesDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Ride Details"

        //binding.personDetail.txtViewEmail.text = "hello"
        if(intent.hasExtra("TripInfo")){
            val trip = intent.getSerializableExtra("TripInfo") as? TripsInfo
            val user = intent.getSerializableExtra("UserInfo") as? userInfo
            binding.rideDetail.txtViewFromDetail.text = trip?.from
            binding.rideDetail.txtViewToDetail.text = trip?.to
            binding.rideDetail.txtViewDescDetail.text = trip?.desc
            binding.rideDetail.txtViewTimeDetail.text = trip?.time
            binding.rideDetail.txtViewDateDetail.text = trip?.date

            binding.personDetail.txtViewNameDetail.text = user?.fullName
            binding.personDetail.txtViewEmailDetail.text = user?.email
            binding.personDetail.txtViewPhoneDetail.text = user?.phoneNumber
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}