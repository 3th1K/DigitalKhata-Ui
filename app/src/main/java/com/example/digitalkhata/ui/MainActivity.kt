package com.example.digitalkhata.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.digitalkhata.R
import com.example.digitalkhata.api.RetrofitClient
import com.example.digitalkhata.model.UserLoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.digitalkhata.api.ApiService
import com.example.digitalkhata.model.UserResponse

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize EditText fields
        usernameEditText = findViewById(R.id.username_input)
        passwordEditText = findViewById(R.id.password_input)
    }

    private fun setupClickListeners() {
        val loginButton = findViewById<Button>(R.id.login_btn)
        loginButton.setOnClickListener { handleLogin() }

        val registerButton = findViewById<Button>(R.id.register_btn)
        registerButton.setOnClickListener { navigateToRegistration() }
    }

    private fun handleLogin() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty()) {
            val loginRequest = UserLoginRequest(username, password)
            login(loginRequest)
        } else {
            showToast("Please enter username and password")
        }
    }

    private fun navigateToRegistration() {
        val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun login(loginRequest: UserLoginRequest) {
        val apiService = RetrofitClient.apiService
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.login(loginRequest)
                if (response.isSuccessful) {
                    val token = response.body()?.data as String
                    val userId = extractUserIdFromToken(token)
                    fetchUserDetails(apiService, token, userId)
                    showToast("Login Success, UserId : $userId")
                } else {
                    showToast("Invalid username or password")
                }
            } catch (e: HttpException) {
                handleLoginError(e)
            } catch (e: Exception) {
                handleLoginError(e)
            }
        }
    }

    private fun fetchUserDetails(apiService: ApiService, token: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userDetailsResponse = apiService.getUserDetails("Bearer $token", userId)
                if (userDetailsResponse.isSuccessful) {
                    val userDetails = userDetailsResponse.body()?.data
                    handleUserDetails(userDetails)
                } else {
                    showToast("Failed to fetch user details")
                }
            } catch (e: Exception) {
                showToast("Error fetching user details: ${e.message}")
            }
        }
    }

    private fun handleUserDetails(userDetails: UserResponse?) {
        showToast(
            "Fetched user details " +
                    "\nusername : ${userDetails?.username}" +
                    "\nemail : ${userDetails?.email}" +
                    "\nfull name : ${userDetails?.fullname}"
        )
    }

    private fun handleLoginError(e: Exception) {
        showToast("Login failed: ${e.message}")
    }

    private fun Context.showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }




    private fun extractUserIdFromToken(token: String): Int {
        val jwt: DecodedJWT = JWT.decode(token)
        return jwt.getClaim("userId").asString().toInt()
    }


}