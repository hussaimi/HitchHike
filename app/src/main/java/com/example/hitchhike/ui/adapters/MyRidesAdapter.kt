package com.example.hitchhike.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hitchhike.R
import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo
import com.example.hitchhike.ui.activities.MyRidesActivity

class MyRidesAdapter(
    private val tripList: ArrayList<TripsInfo>,
    private val user: ArrayList<userInfo>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MyRidesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRidesAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.my_rides_trips, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentTripItem = tripList[position]
        val currentUserItem = user[position]
        holder.from.text = currentTripItem.from
        holder.to.text = currentTripItem.to
        holder.data.text = currentTripItem.date.toString()
        holder.time.text = currentTripItem.time.toString()
        holder.travelingWith.text = currentUserItem.fullName.toString()
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val from: TextView = itemView.findViewById(R.id.textViewCardFrom)
        val to: TextView = itemView.findViewById(R.id.textViewCardTo)
        val data: TextView = itemView.findViewById(R.id.textViewCardDate)
        val time: TextView = itemView.findViewById(R.id.textViewCardTime)
        val travelingWith: TextView = itemView.findViewById(R.id.textViewCardTravelWith)
        val detailButton: Button = itemView.findViewById(R.id.btnCardDetails)

        init {
            detailButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}