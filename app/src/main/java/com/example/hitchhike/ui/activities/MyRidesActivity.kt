
package com.example.hitchhike.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hitchhike.R
import com.example.hitchhike.databinding.ActivityMyRidesBinding
import com.example.hitchhike.model.ScheduleRequestInfo

class MyRidesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRidesBinding
    private lateinit var myRidesArrayList: ArrayList<ScheduleRequestInfo>
    private lateinit var myRidesKeyArrayList: ArrayList<String>
    private val TAG = "MyRides"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRidesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "My Rides"

        myRidesArrayList = arrayListOf()
        myRidesKeyArrayList = arrayListOf()

        if(intent.hasExtra("myRides")){
            myRidesArrayList = intent.getSerializableExtra("myRides") as ArrayList<ScheduleRequestInfo>
            Log.i(TAG, myRidesArrayList.size.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}