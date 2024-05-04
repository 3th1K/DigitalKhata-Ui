package com.example.digitalkhata.model

data class ApiResponse(
    var success: Boolean = false,
    var message: String = "No message",
    var data: Any? = null,
    var errorCode: Int? = null
)
