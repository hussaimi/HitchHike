package com.example.hitchhike

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener

class MyAdapter(private val tripList: ArrayList<TripsInfo>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

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

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val from : TextView = itemView.findViewById(R.id.textViewCardFrom)
        val to : TextView = itemView.findViewById(R.id.textViewCardTo)
        val data : TextView = itemView.findViewById(R.id.textViewCardDate)
        val time : TextView = itemView.findViewById(R.id.textViewCardTime)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}