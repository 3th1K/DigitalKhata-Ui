package com.example.digitalkhata.model

data class ExpenseAddRequest(
    var payerUserId: Int,
    var payeeUserId: Int,
    var amount: Double,
    var description: String?
)

