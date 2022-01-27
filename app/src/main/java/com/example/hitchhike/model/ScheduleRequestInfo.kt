package com.example.hitchhike.model

import java.io.Serializable

data class ScheduleRequestInfo(
    var requesterId: String? = null,
    var tripOwnerId: String? = null,
    var rideId: String? = null,
    var status: String? = null
) : Serializable
