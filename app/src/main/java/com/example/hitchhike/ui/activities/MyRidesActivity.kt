package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hitchhike.databinding.ActivityMyRidesBinding
import com.example.hitchhike.model.ScheduleRequestInfo
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo
import com.example.hitchhike.ui.adapters.MyRidesAdapter
import com.example.hitchhike.ui.interfaces.MyRidesCallBack
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyRidesActivity : AppCompatActivity(), MyRidesAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMyRidesBinding
    private lateinit var myRidesArrayList: ArrayList<ScheduleRequestInfo>
    private lateinit var myRidesKeyArrayList: ArrayList<String>
    private lateinit var dbReference: DatabaseReference
    private lateinit var travelerInfo: ArrayList<userInfo>
    private lateinit var tripInfo: ArrayList<TripsInfo>
    private val TAG = "MyRides"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRidesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "My Rides"

        dbReference = Firebase.database.reference
        myRidesArrayList = arrayListOf()
        myRidesKeyArrayList = arrayListOf()
        travelerInfo = arrayListOf()
        tripInfo = arrayListOf()

        binding.myRidesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.myRidesRecyclerView.setHasFixedSize(true)

        if (intent.hasExtra("myRides")) {
            myRidesArrayList =
                intent.getSerializableExtra("myRides") as ArrayList<ScheduleRequestInfo>
            getMyRides(object : MyRidesCallBack {
                override fun onCallback(trip: TripsInfo?, user: userInfo?) {
                    if (trip != null) {
                        tripInfo.add(trip)
                    }
                    if (user != null) {
                        travelerInfo.add(user)
                    }
                    if (tripInfo.size == myRidesArrayList.size && travelerInfo.size == myRidesArrayList.size) {
                        binding.myRidesRecyclerView.adapter =
                            MyRidesAdapter(tripInfo, travelerInfo, this@MyRidesActivity)
                    }
                }
            }, myRidesArrayList)
        }
    }

    private fun getMyRides(myCallback: MyRidesCallBack, rides: ArrayList<ScheduleRequestInfo>) {
        for (rides in myRidesArrayList) {
            getValueFromDateBase(myCallback, rides, "trips")
            if (rides.requesterId == FirebaseAuth.getInstance().uid) {
                getValueFromDateBase(myCallback, rides, rides.tripOwnerId.toString())
            } else if (rides.tripOwnerId == FirebaseAuth.getInstance().uid) {
                getValueFromDateBase(myCallback, rides, rides.requesterId.toString())
            }
        }

    }

    private fun getValueFromDateBase(
        myCallback: MyRidesCallBack,
        rides: ScheduleRequestInfo,
        identifier: String
    ) {
        when (identifier) {
            "trips" -> dbReference.child("Trips").child(rides.rideId.toString()).get()
                .addOnSuccessListener {
                    var trip = it.getValue(TripsInfo::class.java)!!
                    if (trip != null) {
                        //tripInfo.add(trip)
                        myCallback.onCallback(trip, null)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            rides.tripOwnerId.toString() -> dbReference.child("Users")
                .child(rides.tripOwnerId.toString()).get()
                .addOnSuccessListener {
                    var user = it.getValue(userInfo::class.java)!!
                    if (user != null) {
                        //travelerInfo.add(user)
                        myCallback.onCallback(null, user)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            rides.requesterId.toString() -> dbReference.child("Users")
                .child(rides.requesterId.toString()).get()
                .addOnSuccessListener {
                    var user = it.getValue(userInfo::class.java)!!
                    if (user != null) {
                        //travelerInfo.add(user)
                        myCallback.onCallback(null, user)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, MyRidesDetailActivity::class.java)
        intent.putExtra("TripInfo", tripInfo[position])
        intent.putExtra("UserInfo", travelerInfo[position])
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}