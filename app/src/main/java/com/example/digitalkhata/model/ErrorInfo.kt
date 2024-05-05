package com.example.digitalkhata.model

data class ErrorInfo(
    var error: String = "",
    var errorCode: Int,
    var errorMessage: String = "",
    var errorDescription: String = "",
    var errorSolution: String = ""
)
