package com.example.digitalkhata.util

import com.example.digitalkhata.model.ApiResponse
import com.example.digitalkhata.model.ErrorInfo
import com.google.gson.Gson
import retrofit2.Response

object ErrorResolver {
    fun getError(response: Response<*>) : ErrorInfo?
    {
        val errorBody = response.errorBody()?.string()
        val gson = Gson()
        val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
        val errorInfo = errorResponse.error
        return errorInfo
    }
}