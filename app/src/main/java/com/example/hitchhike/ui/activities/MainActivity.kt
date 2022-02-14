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
import com.example.hitchhike.ui.adapters.MyAdapter
import com.example.hitchhike.R
import com.example.hitchhike.databinding.ContentMainBinding
import com.example.hitchhike.model.ScheduleRequestInfo
import com.example.hitchhike.model.TripsInfo
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : AppCompatActivity(), MyAdapter.OnItemClickListener, OnNavigationItemSelectedListener {

    private val radioButtonDriver: RadioButton by lazy { findViewById(R.id.radioBtnDriver) }
    private val radioButtonRider: RadioButton by lazy { findViewById(R.id.radioBtnRider) }
    private var userType: String? = null
    private val fromLocation: EditText by lazy { findViewById(R.id.editTextFrom) }
    private val toLocation: EditText by lazy { findViewById(R.id.editTextTo) }
    private lateinit var dbReference: DatabaseReference
    private lateinit var scheduleDbReference: DatabaseReference
    private lateinit var tripRecyclerView: RecyclerView
    private lateinit var tripArrayList: ArrayList<TripsInfo>
    private lateinit var tripIdArrayList: ArrayList<String>
    private lateinit var scheduleRequestArrayList: ArrayList<ScheduleRequestInfo>
    private lateinit var scheduleRequestKeyArrayList: ArrayList<String>
    private val drawerLayout: DrawerLayout by lazy { findViewById(R.id.drawerLayout) }
    private lateinit var userIs: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        //retrieve intent from homePageActivity
        if(intent.hasExtra("userIs")){
            userIs = intent.getStringExtra("userIs").toString()
        }
        Toast.makeText(this, userIs, Toast.LENGTH_SHORT).show()

        //initializing scheduleRequestArrayList variable
        scheduleRequestArrayList = arrayListOf()
        scheduleRequestKeyArrayList = arrayListOf()

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

        tripArrayList = arrayListOf()
        tripIdArrayList = arrayListOf()
        dbReference = FirebaseDatabase.getInstance().getReference("Trips")

        // populating dashboard with all the trips available in database
        getTripData()

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
                userType.isNullOrEmpty() -> {
                    Toast.makeText(this, "Please select 'Looking For'", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else -> {
                    getTripData()
                }
            }
        }
        val clearFilterBtn = findViewById<Button>(R.id.btnClearFilter)
        clearFilterBtn.setOnClickListener {
            fromLocation.text = null
            toLocation.text = null
            radioButtonDriver.isChecked = false
            radioButtonRider.isChecked = false
            userType = null
            tripArrayList.clear()
            tripIdArrayList.clear()
            getTripData()
        }

        val addTripButton = findViewById<Button>(R.id.btnAddTrip)
        addTripButton.setOnClickListener {
            val intent = Intent(this, AddTripActivity::class.java).apply {
            }
            startActivity(intent)
        }

        scheduleDbReference = FirebaseDatabase.getInstance().getReference("ScheduleRequests")
        getRequestData()

    }

    /*  get MyRidesData()
    *       1. Read data from schedule request table
    *       2. for all those data where status changed to accepted, see if the requesterId, or tripOwnerId is equal to uid
    *       3. If yes then send requesterId, uid, and tripId to the myRides page */

    private fun getRequestData() {
        scheduleDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scheduleRequestArrayList.clear()
                if (snapshot.exists()) {
                    for (scheduleSnapshot in snapshot.children) {
                        val request = scheduleSnapshot.getValue(ScheduleRequestInfo::class.java)
                        if (request != null) {
                            if(request.tripOwnerId == FirebaseAuth.getInstance().uid.toString() && request.status == "pending"){
                                scheduleRequestArrayList.add(request)
                                scheduleRequestKeyArrayList.add(scheduleSnapshot.key.toString())
                            }

                            // if request.status is changed to decline, then store request data to another list to show user the notification
                            // that the ride request has been declined.

                            /* if the trip has expired and the status is changed to remove then delete
                               .. that instance from the scheduleRequest table */
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

        })
    }

    private fun getTripData() {
        /* while fetching data for all the trips, check if the dates for particular rides are still valid
           if not valid then call removeNode() function to remove that node from the trips table
           Also store the Id of all the remove nodes inside a list
           Use those keys from the removed trips list, and fetch each node from scheduleRequest table containing that tripId
           change the status of those requests to "removed"
           */
        dbReference.addValueEventListener(object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                tripArrayList.clear()
                if (snapshot.exists()){
                    for(tripSnapshot in snapshot.children){
                        val trip = tripSnapshot.getValue(TripsInfo::class.java)
                        if(fromLocation.text.isEmpty()  && toLocation.text.isEmpty() ) { //&& userType.isNullOrEmpty()
                            if (trip != null) {
                                if(!(trip.userType.equals(userIs))){
                                    Log.i("userType", "Match")
                                    checkTripExpiration(trip, tripSnapshot.key.toString())
                                    tripIdArrayList.add(tripSnapshot.key.toString())
                                    tripArrayList.add(trip)
                                }
                            }
                        } else if (trip != null) {
                            if(!(trip.userType.equals(userIs))){
                                if (trip.from.equals(fromLocation.text.toString()) && trip.to.equals(toLocation.text.toString())){ //&& trip.userType.equals(userType)
                                    tripArrayList.clear()
                                    tripIdArrayList.clear()
                                    tripIdArrayList.add(tripSnapshot.key.toString())
                                    tripArrayList.add(trip)
                                }
                            }
                        }
                    }
                    tripRecyclerView.adapter = MyAdapter(tripArrayList, this@MainActivity)
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
    private fun checkTripExpiration(trip: TripsInfo, key: String) {
        val currentTime = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        val formattedCurrentTime = LocalTime.parse(currentTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        val tripTime = trip.time?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))
        val formattedTripTime = LocalTime.parse(tripTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))

        val currentDate = LocalDate.now()
        val tripDate = LocalDate.parse(trip.date, DateTimeFormatter.ofPattern("MM/dd/yyyy")) // yyyy-MM-dd

        if(tripDate.isBefore(currentDate)){
            Log.i("Ride", "Expired")
        } else if(tripDate.isEqual(currentDate)){
            if (formattedTripTime.isBefore(formattedCurrentTime)) {
                Log.i("Ride", "Expired")
            } else {
                Log.i("Ride", "Valid")
            }
        } else if (tripDate.isAfter(currentDate)){
            Log.i("Ride", "Valid")
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, TripDetailActivity::class.java)
        if(tripArrayList[position].userId == FirebaseAuth.getInstance().uid){
            intent.putExtra("ownerOfTrip", "true")
        }
        intent.putExtra("TripInfo", tripArrayList[position])
        intent.putExtra("TripOwnerId", tripArrayList[position].userId)
        intent.putExtra("TripId", tripIdArrayList[position])
        startActivity(intent)
    }


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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        when(item.itemId){
            R.id.actionProfile -> startActivity(Intent(this, MyProfileActivity::class.java))
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

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}