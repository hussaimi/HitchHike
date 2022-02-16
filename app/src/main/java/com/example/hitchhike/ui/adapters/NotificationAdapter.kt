package com.example.hitchhike.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.hitchhike.R
import com.example.hitchhike.model.ScheduleRequestInfo
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationAdapter(private val context: Activity, private val arrayList: ArrayList<ScheduleRequestInfo>) :
    ArrayAdapter<ScheduleRequestInfo> (context, R.layout.list_items, arrayList){

    private lateinit var dbReference: DatabaseReference
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        dbReference = Firebase.database.reference
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.list_items, null)

        val notificationMessage: TextView = view.findViewById(R.id.textViewNotification)

        if(arrayList[position].requesterId == null){
            notificationMessage.text = "No New Notification at the moment"
        } else {
            val requesterId = arrayList[position].requesterId
            if (requesterId != null) {
                dbReference.child("Users").child(requesterId).get().addOnSuccessListener {
                    val user = it.getValue(userInfo::class.java)
                    if (user != null) {
                        if(arrayList[position].status == "pending"){
                            notificationMessage.text = "Ride Request From " + user.fullName.toString()
                        } else if (arrayList[position].status == "decline"){
                            notificationMessage.text = "Ride Request Declined From " + user.fullName.toString()
                        }
                    }
                }.addOnFailureListener{
//                    Log.e("firebase", "Error getting data", it)
                }
            }
        }
        return view
    }

}