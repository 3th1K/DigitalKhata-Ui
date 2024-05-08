package com.example.digitalkhata.model

data class UserTransactionHistory(
    var transactions: List<ExpenseResponse> = listOf(),
    var netAmountOwedOrReceived: Map<Int, Double> = mapOf()
)

