package com.example.hitchhike.model

import java.io.Serializable

data class userInfo (
    var fullName: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var userId: String? = null
) : Serializable