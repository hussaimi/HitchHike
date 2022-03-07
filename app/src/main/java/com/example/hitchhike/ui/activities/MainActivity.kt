package com.example.hitchhike.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hitchhike.ui.adapters.DashboardAdapter
import com.example.hitchhike.R
import com.example.hitchhike.model.ScheduleRequestInfo
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.ui.interfaces.MyCallBack
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


// Don't delete rides, just show those rides which dates are not passed.
// Search for soft-deletes

class MainActivity : AppCompatActivity(), DashboardAdapter.OnItemClickListener, OnNavigationItemSelectedListener {

    private val fromLocation: EditText by lazy { findViewById(R.id.editTextFrom) }
    private val toLocation: EditText by lazy { findViewById(R.id.editTextTo) }
    private lateinit var dbReference: DatabaseReference
    private lateinit var scheduleDbReference: DatabaseReference
    private lateinit var tripRecyclerView: RecyclerView
    private lateinit var validTripArrayList: ArrayList<TripsInfo>
    private lateinit var validTripIdArrayList: ArrayList<String>
    private lateinit var scheduleRequestArrayList: ArrayList<ScheduleRequestInfo>
    private lateinit var scheduleRequestKeyArrayList: ArrayList<String>
    private lateinit var myRidesArrayList: ArrayList<ScheduleRequestInfo>
    private lateinit var myRidesKeyArrayList: ArrayList<String>
    private val drawerLayout: DrawerLayout by lazy { findViewById(R.id.drawerLayout) }
    private lateinit var userIs: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        title = "Dashboard"

        //retrieve userType (Driver/Rider) from homePageActivity
        if(intent.hasExtra("userIs")){
            userIs = intent.getStringExtra("userIs").toString()
        }

        //initializing ArrayList variable
        scheduleRequestArrayList = arrayListOf()
        scheduleRequestKeyArrayList = arrayListOf()
        validTripArrayList = arrayListOf()
        validTripIdArrayList = arrayListOf()
        myRidesArrayList = arrayListOf()
        myRidesKeyArrayList = arrayListOf()

        val navView = findViewById<NavigationView>(R.id.navView)
        navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_Nav_Drawer, R.string.close_Nav_Drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        tripRecyclerView = findViewById(R.id.tripList)
        tripRecyclerView.layoutManager = LinearLayoutManager(this)
        tripRecyclerView.setHasFixedSize(true)


        dbReference = FirebaseDatabase.getInstance().getReference("Trips")
        scheduleDbReference = FirebaseDatabase.getInstance().getReference("ScheduleRequests")
        // populating dashboard with all the trips available in database
        getTripData(object : MyCallBack {
            override fun onCallback(validTrip: ArrayList<String>){
                getRequestData(validTrip)
            }
        })

        val submitButton = findViewById<Button>(R.id.btnFilter)
        submitButton.setOnClickListener {
            when {
                fromLocation.text.isEmpty() -> {
                    fromLocation.error = "Required"
                    return@setOnClickListener
                }
                toLocation.text.isEmpty() -> {
                    toLocation.error = "Required"
                    return@setOnClickListener
                }
                else -> {
                    getTripData(object : MyCallBack {
                        override fun onCallback(validTrip: ArrayList<String>) {
                        }
                    })
                }
            }
        }

        val clearFilterBtn = findViewById<Button>(R.id.btnClearFilter)
        clearFilterBtn.setOnClickListener {
            fromLocation.text = null
            toLocation.text = null
            validTripArrayList.clear()
            validTripIdArrayList.clear()
            getTripData(object : MyCallBack {
                override fun onCallback(validTrip: ArrayList<String>) {
                }
            })
        }

        val addTripButton = findViewById<Button>(R.id.btnAddTrip)
        addTripButton.setOnClickListener {
            val intent = Intent(this, AddTripActivity::class.java).apply {
            }
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        scheduleRequestArrayList.clear()
    }

    /*  get MyRidesData()
    *       1. Read data from schedule request table
    *       2. for all those data where status changed to accepted, see if the requesterId, or tripOwnerId is equal to uid
    *       3. If yes then send requesterId, uid, and tripId to the myRides page */

    private fun getRequestData(validTrip: ArrayList<String>) {
        validTrip.forEach {
            val query = scheduleDbReference.orderByChild("rideId").equalTo(it)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    scheduleRequestArrayList.clear()
                    dataSnapshot.children.forEach { childSnapshot ->
                        val request = childSnapshot.getValue(ScheduleRequestInfo::class.java)
                        if (request != null) {
                            if (request.tripOwnerId == FirebaseAuth.getInstance().uid.toString() && request.status == "pending") {
                                scheduleRequestArrayList.add(request)
                                scheduleRequestKeyArrayList.add(childSnapshot.key.toString())
                            }
                            if (request.requesterId == FirebaseAuth.getInstance().uid.toString() && request.status == "decline" && request.status != "Complete") {
                                scheduleRequestArrayList.add(request)
                                scheduleRequestKeyArrayList.add(childSnapshot.key.toString())
                            }
                            if (request.status == "accept") {
                                myRidesArrayList.add((request))
                                myRidesKeyArrayList.add(childSnapshot.key.toString())
                            }
                        }
                    }
                    if(scheduleRequestArrayList.size != 0){
                        Toast.makeText(this@MainActivity, "You have a notification.", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }

    }

    //only fetch those rides which are not expired yet.
    private fun getTripData(myCallback : MyCallBack) {
        dbReference.addValueEventListener(object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                validTripArrayList.clear()
                validTripIdArrayList.clear()
                if (snapshot.exists()){
                    for(tripSnapshot in snapshot.children){
                        val trip = tripSnapshot.getValue(TripsInfo::class.java)
//                        validTripIdArrayList.add(tripSnapshot.key.toString())
                        if (trip != null) {
                            if(checkTripExpiration(trip, tripSnapshot.key.toString()) == "Valid"){
                                if(fromLocation.text.isEmpty()  && toLocation.text.isEmpty() ) { //&& userType.isNullOrEmpty()
                                    if (trip.lookingFor.equals(userIs)) {
                                        Log.i("userType", "Match")
                                        validTripIdArrayList.add(tripSnapshot.key.toString())
                                        validTripArrayList.add(trip)
                                    }
                                } else
                                    if(trip.lookingFor.equals(userIs)){
                                        if (trip.from.equals(fromLocation.text.toString()) && trip.to.equals(toLocation.text.toString())){ //&& trip.userType.equals(userType)
                                            validTripArrayList.clear()
                                            validTripIdArrayList.clear()
                                            validTripIdArrayList.add(tripSnapshot.key.toString())
                                            validTripArrayList.add(trip)
                                        }
                                    }
                            }
                        }
                    }
                    myCallback.onCallback(validTripIdArrayList)
                    tripRecyclerView.adapter = DashboardAdapter(validTripArrayList, this@MainActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


    /* Check the expiration of trips based on date and time.
       if (date is passed) {
            ride has expired
       } else if (date is same as today date){
            if(time has passed){
                ride has expired
            } else {
                ride still valid
            }
       } else {
            ride still valid
       }
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkTripExpiration(trip: TripsInfo, key: String): String {
        val currentTime = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        val formattedCurrentTime = LocalTime.parse(currentTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        val tripTime = trip.time?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        val formattedTripTime = LocalTime.parse(tripTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))

        val currentDate = LocalDate.now()
        val tripDate = LocalDate.parse(trip.date.toString(), DateTimeFormatter.ofPattern("MM/dd/yyyy")) // yyyy-MM-dd

        Log.i("Ride", formattedCurrentTime.toString())
        Log.i("Ride", formattedTripTime.toString())
        if(tripDate.isBefore(currentDate)){
            Log.i("Ride", "Expired")
            return "Expired"
        } else if(tripDate.isEqual(currentDate)){
            if (formattedTripTime.isBefore(formattedCurrentTime)) {
                Log.i("Ride", "Expired")
                return "Expired"
            } else {
                Log.i("Ride", "Valid: Same date but later time.")
                return "Valid" // I was returning "Expried" instead of valid (my stupid ass)
            }
        } else if (tripDate.isAfter(currentDate)){
            Log.i("Ride", "Valid: After today")
            return "Valid"
        }
        return "Valid"
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, TripDetailActivity::class.java)
        if(validTripArrayList[position].userId == FirebaseAuth.getInstance().uid){
            intent.putExtra("ownerOfTrip", "true")
        }
        intent.putExtra("TripInfo", validTripArrayList[position])
        intent.putExtra("TripOwnerId", validTripArrayList[position].userId)
        intent.putExtra("TripId", validTripIdArrayList[position])
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        when(item.itemId){
            R.id.actionProfile -> startActivity(Intent(this, MyProfileActivity::class.java))
            R.id.actionMyRides -> openMyRidesPage()
            R.id.actionNotification -> goToNotificationPage()
            R.id.actionLogout -> logout()
        }
        return true
    }

    private fun goToNotificationPage() {
        val intent = Intent(this, NotificationsActivity::class.java)
        if(scheduleRequestArrayList.isNotEmpty()){
            intent.putExtra("requests", scheduleRequestArrayList)
            intent.putExtra("requestKeys", scheduleRequestKeyArrayList)
        }
        startActivity(intent)
    }

    private fun openMyRidesPage(){
        val intent = Intent(this, MyRidesActivity::class.java)
        if(myRidesArrayList.isNotEmpty()){
            intent.putExtra("myRides", myRidesArrayList)
            intent.putExtra("myRideKeys", myRidesKeyArrayList)
        }
        startActivity(intent)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}


/*scheduleDbReference.addValueEventListener(object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        scheduleRequestArrayList.clear()

        // search for only those request where trip is valid
        // query for all schedule requests based on ValidTripArrayList
        if (snapshot.exists()) {
            for (scheduleSnapshot in snapshot.children) {
                val request = scheduleSnapshot.getValue(ScheduleRequestInfo::class.java)
                if (request != null) {
                    if(request.tripOwnerId == FirebaseAuth.getInstance().uid.toString() && request.status == "pending"){
                        scheduleRequestArrayList.add(request)
                        scheduleRequestKeyArrayList.add(scheduleSnapshot.key.toString())
                    }
                    if(request.requesterId == FirebaseAuth.getInstance().uid.toString() && request.status == "decline" && request.status != "Complete"){
                        scheduleRequestArrayList.add(request)
                        scheduleRequestKeyArrayList.add(scheduleSnapshot.key.toString())
                    }


                    // if request.status is changed to decline, then store request data to another list to show user the notification
                    // that the ride request has been declined.

                    *//* if the trip has expired and the status is changed to remove then delete
                               .. that instance from the scheduleRequest table *//*
                        }
                    }
                }
                if(scheduleRequestArrayList.size != 0){
                    Toast.makeText(this@MainActivity, "You have a notification.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })*/
/*
        scheduleDbReference = FirebaseDatabase.getInstance().getReference("ScheduleRequests")
        val query = scheduleDbReference.orderByChild("rideId").equalTo("-Mw2u5Paxbg_eUzlMoPr")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { childSnapshot ->
//                    Toast.makeText(
//                        this@MainActivity,
//                        childSnapshot.key,
//                        Toast.LENGTH_SHORT
//                    ).show()

                    Log.i("name", childSnapshot.key.toString())
                    //val textValue = childSnapshot.child("text").getValue(String::class.java)
                }
            }
        })

 */
//    fun onRadioButtonClicked(view: View){
//        if (view is RadioButton) {
//            // Is the button now checked?
//            val checked = view.isChecked
//
//            // Check which radio button was clicked
//            when (view.getId()) {
//                radioButtonDriver.id ->
//                    if (checked) {
//                        radioButtonRider.isChecked = false
//                        userType = "Driver"
//                    }
//                radioButtonRider.id ->
//                    if (checked) {
//                        radioButtonDriver.isChecked = false
//                        userType = "Rider"
//                    }
//            }
//        }
//    }