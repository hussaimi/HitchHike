package com.example.hitchhike.ui.activities

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hitchhike.databinding.ActivityMyRidesDetailBinding
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo
import java.util.*

class MyRidesDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRidesDetailBinding
    private lateinit var trip: TripsInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRidesDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Ride Details"

        if (intent.hasExtra("TripInfo")) {
            trip = (intent.getSerializableExtra("TripInfo") as? TripsInfo)!!
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

        binding.btnNavigate.setOnClickListener {
            var gc = Geocoder(this, Locale.getDefault())
            var fromAddresses: MutableList<Address> = gc.getFromLocationName(trip?.from, 2)
            var toAddresses: MutableList<Address> = gc.getFromLocationName(trip?.to, 2)
            var from: Address = fromAddresses[0]
            var to: Address = toAddresses[0]

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=${from.latitude},${from.longitude}&daddr=${to.latitude},${to.longitude}")
            )
            startActivity(intent)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}