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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("Identity/login")
    suspend fun login(
        @Body requestBody: UserLoginRequest
    ): Response<ApiResponse<String>>

    @POST("Identity/register")
    suspend fun register(
        @Body requestBody: UserRegisterRequest
    ): Response<ApiResponse<String>>

    @GET("User/profile/{userId}")
    suspend fun getUserDetails(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<ApiResponse<UserResponse>>

    @GET("User/search/{searchQuery}")
    suspend fun searchUser(
        @Header("Authorization") token: String,
        @Path("searchQuery") searchQuery: String
    ): Response<ApiResponse<List<UserResponse>>>

    @GET("Expense/expense-users/{userId}")
    suspend fun getUserExpenses(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<ApiResponse<List<UserResponse>>>

    @GET("Expense/{userId}/transaction-history/{otherUserId}")
    suspend fun getTransactionHistory(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Path("otherUserId") otherUserId: Int
    ): Response<ApiResponse<UserTransactionHistory>>

    @POST("Expense/add")
    suspend fun addExpense(
        @Header("Authorization") token: String,
        @Body requestBody: ExpenseAddRequest
    ): Response<ApiResponse<ExpenseResponse>>

    @DELETE("Expense/delete/{id}")
    suspend fun deleteExpense(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<ApiResponse<ExpenseResponse>>
}