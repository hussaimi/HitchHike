package com.example.hitchhike.model

import java.io.Serializable
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalTime


data class TripsInfo(
    var from: String? = null,
    var to: String? = null,
    var desc: String? = null,
    var date: String? = null,
    var time: String? = null,
    var noOfPeople: String? = null,
    var lookingFor: String? = null,
    var userId: String? = null) : Serializable
//add userId column as well to keep record of which user posted a ride.