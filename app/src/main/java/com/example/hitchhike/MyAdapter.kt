package com.example.hitchhike

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val tripList: ArrayList<TripsInfo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.trip_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = tripList[position]
        holder.from.text = currentItem.from
        holder.to.text = currentItem.to
        holder.data.text = currentItem.date
        holder.time.text = currentItem.time
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val from : TextView = itemView.findViewById(R.id.textViewCardFrom)
        val to : TextView = itemView.findViewById(R.id.textViewCardTo)
        val data : TextView = itemView.findViewById(R.id.textViewCardDate)
        val time : TextView = itemView.findViewById(R.id.textViewCardTime)
    }

}