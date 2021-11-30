package com.example.hitchhike.model

import java.io.Serializable

data class TripsInfo(
    var from: String? = null,
    var to: String? = null,
    var desc: String? = null,
    var date: String? = null,
    var time: String? = null,
    var noOfPeople: String? = null,
    var userType: String? = null) : Serializable
