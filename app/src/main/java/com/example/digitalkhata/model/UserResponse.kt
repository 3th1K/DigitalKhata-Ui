package com.example.digitalkhata.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserResponse(
    var userId: Int,
    var username: String,
    var email: String,
    var fullname: String
): Parcelable
