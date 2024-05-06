package com.example.digitalkhata.api
import com.example.digitalkhata.model.ApiResponse
import com.example.digitalkhata.model.UserLoginRequest
import com.example.digitalkhata.model.UserRegisterRequest
import com.example.digitalkhata.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(
        @Body requestBody: UserLoginRequest
    ): Response<ApiResponse<String>>

    @POST("register")
    suspend fun register(
        @Body requestBody: UserRegisterRequest
    ): Response<ApiResponse<String>>

    @GET("user/profile/{userId}")
    suspend fun getUserDetails(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<ApiResponse<UserResponse>>
}