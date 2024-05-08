package com.example.digitalkhata.model

import java.util.Date

data class ExpenseResponse(
    var expenseId: Int,
    var payerUserId: Int,
    var payeeUserId: Int,
    var amount: Double,
    var description: String?,
    var date: Date
)