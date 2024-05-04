package com.example.digitalkhata.api
import com.example.digitalkhata.model.ApiResponse
import com.example.digitalkhata.model.UserLoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
interface ApiService {
    @POST("login")
    suspend fun login(@Body requestBody: UserLoginRequest): Response<ApiResponse>

    // Define other API endpoints here
}