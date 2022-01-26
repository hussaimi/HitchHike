package com.example.hitchhike.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hitchhike.ui.adapters.MyAdapter
import com.example.hitchhike.R
import com.example.hitchhike.model.TripsInfo
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), MyAdapter.OnItemClickListener, OnNavigationItemSelectedListener {

    private val radioButtonDriver: RadioButton by lazy { findViewById(R.id.radioBtnDriver) }
    private val radioButtonRider: RadioButton by lazy { findViewById(R.id.radioBtnRider) }
    private var userType: String? = null
    private val fromLocation: EditText by lazy { findViewById(R.id.editTextFrom) }
    private val toLocation: EditText by lazy { findViewById(R.id.editTextTo) }
    private lateinit var dbReference: DatabaseReference
    private lateinit var tripRecyclerView: RecyclerView
    private lateinit var tripArrayList: ArrayList<TripsInfo>
    private lateinit var tripIdArrayList: ArrayList<String>
    private val drawerLayout: DrawerLayout by lazy { findViewById(R.id.drawerLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Toast current userId from Firebase.
        Toast.makeText(this, FirebaseAuth.getInstance().uid.toString(), Toast.LENGTH_SHORT).show()

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


    }

    private fun getTripData() {
        dbReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                tripArrayList.clear()
                if (snapshot.exists()){
                    for(tripSnapshot in snapshot.children){
                        val trip = tripSnapshot.getValue(TripsInfo::class.java)
                        if(fromLocation.text.isEmpty()  && toLocation.text.isEmpty() && userType.isNullOrEmpty()) {
                            tripIdArrayList.add(tripSnapshot.key.toString())
                            tripArrayList.add(trip!!)
                        } else if (trip != null) {
                            if (trip.from.equals(fromLocation.text.toString()) && trip.to.equals(toLocation.text.toString()) && trip.userType.equals(userType)){
                                tripArrayList.clear()
                                tripIdArrayList.clear()
                                tripIdArrayList.add(tripSnapshot.key.toString())
                                tripArrayList.add(trip)
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

    override fun onItemClick(position: Int) {
        val intent = Intent(this, TripDetailActivity::class.java)
        //Toast.makeText(this, tripIdArrayList[position], Toast.LENGTH_SHORT).show()
        Toast.makeText(this, tripArrayList[position].userId, Toast.LENGTH_SHORT).show()
        intent.putExtra("TripInfo", tripArrayList[position])
        startActivity(intent)
    }


    fun onRadioButtonClicked(view: View){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                radioButtonDriver.id ->
                    if (checked) {
                        radioButtonRider.isChecked = false
                        userType = "Driver"
                    }
                radioButtonRider.id ->
                    if (checked) {
                        radioButtonDriver.isChecked = false
                        userType = "Rider"
                    }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        when(item.itemId){
            R.id.actionProfile -> startActivity(Intent(this, MyProfileActivity::class.java))
            R.id.actionNotification -> startActivity(Intent(this, NotificationsActivity::class.java))
            R.id.actionLogout -> logout()
        }
        return true
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}