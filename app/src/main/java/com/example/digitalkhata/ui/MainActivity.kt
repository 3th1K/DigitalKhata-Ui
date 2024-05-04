package com.example.digitalkhata.ui

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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // Handle click event for the login button
        val loginButton = findViewById<Button>(R.id.login_btn)
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if username and password are not empty
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Create request body
                val loginRequest = UserLoginRequest(username, password);

                // Make login API call
                val apiService = RetrofitClient.apiService
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = apiService.login(loginRequest)
                        if (response.isSuccessful) {
                            val token = response.body()?.data as String
                            // Handle successful login, e.g., save token and navigate to next screen
                            //navigateToNextScreen(token)
                            showToast("Login Success")
                        } else {
                            // Handle unsuccessful login (e.g., display error message)
                            showToast("Invalid username or password")
                        }
                    } catch (e: HttpException) {
                        // Handle HTTP errors (e.g., 401 Unauthorized)
                        showToast("Login failed: ${e.message}")
                    } catch (e: Exception) {
                        // Handle other exceptions (e.g., network errors)
                        showToast("Login failed: ${e.message}")
                    }
                }
            } else {
                showToast("Please enter username and password")
            }
        }

        // Handle click event for the register button
        val registerButton = findViewById<Button>(R.id.register_btn)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

}