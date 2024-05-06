package com.example.digitalkhata.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
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
import com.example.digitalkhata.util.LocalStorage
import com.example.digitalkhata.util.TokenService

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var loadingLayout: RelativeLayout
    private lateinit var mainLayout: RelativeLayout
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setupUI()
        setupClickListeners()
    }

    private fun setupUI()
    {
        if(!TokenService.isUserLoggedIn(this@MainActivity)) {

            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
            { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            // Initialize EditText fields
            usernameEditText = findViewById(R.id.username_input)
            passwordEditText = findViewById(R.id.password_input)
            loginButton = findViewById(R.id.login_btn)
            registerButton = findViewById(R.id.register_btn)
            loadingLayout = findViewById(R.id.loading_layout)
            mainLayout = findViewById(R.id.main)
        }
        else
        {
            navigateToDashboard()
            finish()
        }
    }

    private fun showLoading() {
        runOnUiThread {
            // Disable user interaction for all views in the main layout
            usernameEditText.isEnabled = false
            passwordEditText.isEnabled = false
            loginButton.isEnabled = false
            registerButton.isEnabled = false
            // Optionally, apply a semi-transparent overlay to visually indicate that the views are disabled
            mainLayout.alpha = 0.5f
            // Show the loading layout
            loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        runOnUiThread {
            // Enable user interaction for all views in the main layout
            usernameEditText.isEnabled = true
            passwordEditText.isEnabled = true
            loginButton.isEnabled = true
            registerButton.isEnabled = true
            // Restore the alpha of the main layout
            mainLayout.alpha = 1.0f
            // Hide the loading layout
            loadingLayout.visibility = View.GONE
        }
    }



    private fun setupClickListeners()
    {
        loginButton.setOnClickListener {
            handleLogin()
        }

        registerButton.setOnClickListener {
            navigateToRegistration()
        }
    }

    private fun handleLogin()
    {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty())
        {
            val loginRequest = UserLoginRequest(username, password)
            login(loginRequest)
        }
        else
        {
            showToast("Please enter username and password")
        }
    }

    private fun navigateToRegistration()
    {
        val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDashboard()
    {
        val intent = Intent(this@MainActivity, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun login(loginRequest: UserLoginRequest)
    {
        showLoading()
        val apiService = RetrofitClient.apiService
        CoroutineScope(Dispatchers.IO).launch {
            try
            {
                val response = apiService.login(loginRequest)
                if (response.isSuccessful)
                {
                    val token = response.body()?.data as String
                    if(token.isNotEmpty())
                    {
                        LocalStorage.saveAuthToken(this@MainActivity, token)
                        val userId = extractUserIdFromToken(token)
                        fetchUserDetails(apiService, token, userId)
                    }
                    else
                    {
                        hideLoading()
                        showToast("Login Failed")
                    }
                }
                else
                {
                    hideLoading()
                    showToast("Invalid username or password")
                }
            }
            catch (e: HttpException)
            {
                handleLoginError(e)
            }
            catch (e: Exception)
            {
                handleLoginError(e)
            }
        }
    }

    private fun fetchUserDetails(apiService: ApiService, token: String, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try
            {
                val userDetailsResponse = apiService.getUserDetails("Bearer $token", userId)
                if (userDetailsResponse.isSuccessful)
                {
                    val userDetails = userDetailsResponse.body()?.data
                    handleUserDetails(userDetails)
                }
                else
                {
                    hideLoading()
                    showToast("Failed to fetch user details")
                }
            } catch (e: Exception) {
                hideLoading()
                showToast("Error fetching user details: ${e.message}")
            }
        }
    }

    private fun handleUserDetails(userDetails: UserResponse?) {
        if(userDetails != null)
        {
            LocalStorage.saveUserDetails(this@MainActivity, userDetails)
            hideLoading()
            navigateToDashboard()
        }
        else
        {
            hideLoading()
            showToast("Cannot fetch user details")
        }

    }

    private fun handleLoginError(e: Exception) {
        hideLoading()
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