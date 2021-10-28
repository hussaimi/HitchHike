package com.example.hitchhike

data class TripsInfo(
    var From: String,
    var To: String,
    var Desc: String,
    var Date: String,
    var Time: String,
    var NoOfPeople: String? = null,
    var userType: String? = null){

}
