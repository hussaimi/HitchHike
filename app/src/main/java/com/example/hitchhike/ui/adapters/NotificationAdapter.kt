package com.example.hitchhike.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.hitchhike.R
import com.example.hitchhike.model.NotificationsInfo

class NotificationAdapter(private val context: Activity, private val arrayList: ArrayList<NotificationsInfo>) :
    ArrayAdapter<NotificationsInfo> (context, R.layout.list_items, arrayList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.list_items, null)

        val notificationMessage: TextView = view.findViewById(R.id.textViewNotification)

        notificationMessage.text = "Ride Request From " + arrayList[position].senderID

        return view
    }

}