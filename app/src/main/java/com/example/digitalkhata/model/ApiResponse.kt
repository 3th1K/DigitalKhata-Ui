package com.example.digitalkhata.model

data class ApiResponse<T>(
    var success: Boolean = false,
    var message: String = "No message",
    var data: T? = null,
    var error: ErrorInfo? = null
)

