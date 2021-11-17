package com.example.hitchhike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class TripDetails : AppCompatActivity() {

    private val fromDetails: TextView by lazy { findViewById(R.id.txtViewFromDetail) }
    private val toDetails: TextView by lazy { findViewById(R.id.txtViewToDetails) }
    private val descDetails: TextView by lazy { findViewById(R.id.txtViewDescDetail) }
    private val dateDetails: TextView by lazy { findViewById(R.id.txtViewDateDetail) }
    private val timeDetails: TextView by lazy { findViewById(R.id.txtViewTimeDetail) }
    private val noOfPeopleDetails: TextView by lazy { findViewById(R.id.txtViewNoOfPeopleDetail) }
    private val lookingForDetails: TextView by lazy { findViewById(R.id.txtViewLookinForDetail) }
    private val btnSchedule: Button by lazy { findViewById(R.id.btnSchedule) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)
        title = "Trip Details"

        val trip = intent.getSerializableExtra("TripInfo") as? TripsInfo
        if (trip != null) {
            fromDetails.text = trip.from
            toDetails.text = trip.to
            descDetails.text = trip.desc
            dateDetails.text = trip.date
            timeDetails.text = trip.time
            noOfPeopleDetails.text = trip.noOfPeople
            lookingForDetails.text = trip.userType
        }
        btnSchedule.setOnClickListener {

        }
    }
}