package com.example.digitalkhata.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.digitalkhata.R
import com.example.digitalkhata.api.ApiService
import com.example.digitalkhata.api.RetrofitClient
import com.example.digitalkhata.model.UserRegisterRequest
import com.example.digitalkhata.model.UserResponse
import com.example.digitalkhata.util.ErrorResolver
import com.example.digitalkhata.util.LocalStorage
import com.example.digitalkhata.util.TokenService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegistrationActivity : AppCompatActivity() {

    private lateinit var fullnameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var setPasswordEditText: EditText
    private lateinit var rewritePasswordEditText: EditText

    private lateinit var registerButton: Button

    private lateinit var loadingLayout: RelativeLayout
    private lateinit var mainLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setupUI()
        setupClickListeners()
    }

    private fun setupUI()
    {
        setContentView(R.layout.register_ui)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_register))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fullnameEditText = findViewById(R.id.input_fullname)
        usernameEditText = findViewById(R.id.input_username)
        emailEditText = findViewById(R.id.input_email)
        setPasswordEditText = findViewById(R.id.input_set_password)
        rewritePasswordEditText = findViewById(R.id.input_rewrite_password)

        registerButton = findViewById(R.id.btn_register)

        loadingLayout = findViewById(R.id.loading_layout)
        mainLayout = findViewById(R.id.layout_register)
    }

    private fun setupClickListeners()
    {
        registerButton.setOnClickListener {
            handleRegistration()
        }
    }

    private fun handleRegistration()
    {
        val fullname = fullnameEditText.text.toString()
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val setPassword = setPasswordEditText.text.toString()

        if(validateInputs())
        {
            val userRegisterRequest = UserRegisterRequest(username, email, setPassword, fullname)
            register(userRegisterRequest)
        }
        else
        {
            showToast("Input validation failed")
        }
    }

    private fun register(userRegisterRequest: UserRegisterRequest) {
        showLoading()
        val apiService = RetrofitClient.apiService
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.register(userRegisterRequest)
                if (response.isSuccessful)
                {
                    val token = response.body()?.data as String
                    if(token.isNotEmpty())
                    {
                        LocalStorage.saveAuthToken(this@RegistrationActivity, token)
                        val userId = TokenService.extractUserIdFromToken(token)
                        fetchUserDetails(apiService, token, userId)
                    }
                    else
                    {
                        hideLoading()
                        showToast("Registration Failed")
                    }
                }
                else
                {
                    hideLoading()
                    val errorResponse = ErrorResolver.getError(response)
                    if(errorResponse?.error == "err_user_exists")
                    {
                        runOnUiThread{
                            usernameEditText.error = "This username is not available"
                            emailEditText.error = "User already registered with this email"
                        }
                        showToast("User already registered with this username/email")
                    }
                    if(errorResponse?.error == "err_token_generation_failed")
                    {
                        showToast("Registration Failed. Try again later")
                    }

                }
            }
            catch (e: HttpException)
            {
                handleRegisterError(e)
            }
            catch (e: Exception)
            {
                handleRegisterError(e)
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
            LocalStorage.saveUserDetails(this@RegistrationActivity, userDetails)
            hideLoading()
            navigateToDashboard()
        }
        else
        {
            hideLoading()
            showToast("Cannot fetch user details")
        }

    }

    private fun navigateToDashboard()
    {
        val intent = Intent(this@RegistrationActivity, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun handleRegisterError(e: Exception) {
        showToast("Registration failed, try again later. ${e.message}")
    }

    private fun validateInputs() : Boolean
    {
        var isValid  = true
        if(fullnameEditText.text.isEmpty())
        {
            fullnameEditText.error = "Please enter your full name"
            isValid = false
        }
        if(usernameEditText.text.isEmpty())
        {
            usernameEditText.error = "Please enter your username"
            isValid = false
        }
        if(emailEditText.text.isEmpty())
        {
            emailEditText.error = "Please enter your email"
            isValid = false
        }
        if(setPasswordEditText.text.isEmpty())
        {
            setPasswordEditText.error = "Please enter the password"
            isValid = false
        }
        if(rewritePasswordEditText.text.isEmpty())
        {
            rewritePasswordEditText.error = "Please rewrite the same password"
            isValid = false
        }
        if(setPasswordEditText.text.toString() != rewritePasswordEditText.text.toString())
        {
            rewritePasswordEditText.error = "Passwords does not matches"
            isValid = false
        }
        return isValid
    }

    private fun showLoading() {
        runOnUiThread {
            // Disable user interaction for all views in the main layout
            usernameEditText.isEnabled = false
            fullnameEditText.isEnabled = false
            setPasswordEditText.isEnabled = false
            rewritePasswordEditText.isEnabled = false
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
            fullnameEditText.isEnabled = true
            setPasswordEditText.isEnabled = true
            rewritePasswordEditText.isEnabled = true
            registerButton.isEnabled = true
            // Restore the alpha of the main layout
            mainLayout.alpha = 1.0f
            // Hide the loading layout
            loadingLayout.visibility = View.GONE
        }
    }
    private fun Context.showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}