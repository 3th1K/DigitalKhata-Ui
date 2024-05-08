package com.example.digitalkhata.api
import com.example.digitalkhata.model.ApiResponse
import com.example.digitalkhata.model.ExpenseAddRequest
import com.example.digitalkhata.model.ExpenseResponse
import com.example.digitalkhata.model.UserLoginRequest
import com.example.digitalkhata.model.UserRegisterRequest
import com.example.digitalkhata.model.UserResponse
import com.example.digitalkhata.model.UserTransactionHistory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("user/search/{searchQuery}")
    suspend fun searchUser(
        @Header("Authorization") token: String,
        @Path("searchQuery") searchQuery: String
    ): Response<ApiResponse<List<UserResponse>>>

    @GET("expense/users-expenses")
    suspend fun getUserExpenses(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ): Response<ApiResponse<List<UserResponse>>>

    @GET("expense/transaction-history")
    suspend fun getTransactionHistory(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
        @Query("otherUserId") otherUserId: Int
    ): Response<ApiResponse<UserTransactionHistory>>

    @POST("expense/add")
    suspend fun addExpense(
        @Header("Authorization") token: String,
        @Body requestBody: ExpenseAddRequest
    ): Response<ApiResponse<ExpenseResponse>>
}