package com.example.hitchhike.ui.interfaces

import com.example.hitchhike.model.TripsInfo
import com.example.hitchhike.model.userInfo

interface MyRidesCallBack {
    fun onCallback(trip: TripsInfo?, user: userInfo?)
}