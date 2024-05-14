package com.example.digitalkhata.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class ExpenseResponse(
    var expenseId: Int,
    var payerUserId: Int,
    var payeeUserId: Int,
    var amount: Double,
    var description: String?,
    var date: String
) {
    val localDateTime: String // Property to hold local time
        get() {
            val utcFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
            val localFormatter = SimpleDateFormat("dd MMM hh.mm a", Locale.US)

            // Set the time zone for formatters
            utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
            localFormatter.timeZone = TimeZone.getDefault() // Get default time zone of the device

            // Parse UTC time
            val utcDate = utcFormatter.parse(date)

            val out = localFormatter.format(utcDate)
            // Convert to local time
            return out
        }
}