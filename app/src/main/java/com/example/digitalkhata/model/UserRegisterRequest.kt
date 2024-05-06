package com.example.digitalkhata.model

data class UserRegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullname: String
)
