package com.example.digitalkhata.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.digitalkhata.R
import com.example.digitalkhata.api.ApiService
import com.example.digitalkhata.api.RetrofitClient
import com.example.digitalkhata.databinding.FragmentLoginBinding
import com.example.digitalkhata.model.UserLoginRequest
import com.example.digitalkhata.model.UserResponse
import com.example.digitalkhata.util.LocalStorage
import com.example.digitalkhata.util.TokenService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupClickListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        checkLoggedIn()
    }


    private fun checkLoggedIn()
    {
        if(TokenService.isUserLoggedIn(requireContext())) {
            navigateToDashboard()
        }
    }

    private fun navigateToDashboard() {
        activity?.runOnUiThread{
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
        }
    }

    private fun setupClickListeners()
    {
        binding.btnLogin.setOnClickListener{
           handleLogin()
        }

        binding.btnRegister.setOnClickListener{
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
    private fun handleLogin()
    {
        val username = binding.inputUsername.text.toString()
        val password = binding.inputPassword.text.toString()
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
                        LocalStorage.saveAuthToken(requireContext(), token)
                        val userId = TokenService.extractUserIdFromToken(token)
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
            LocalStorage.saveUserDetails(requireContext(), userDetails)
            hideLoading()
            navigateToDashboard()
        }
        else
        {
            hideLoading()
            showToast("Cannot fetch user details")
        }

    }

    private fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleLoginError(e: Exception) {
        hideLoading()
        showToast("Login failed: ${e.message}")
    }

    private fun showLoading() {
        activity?.runOnUiThread {
            // Disable user interaction for all views in the main layout
            binding.inputUsername.isEnabled = false
            binding.inputPassword.isEnabled = false
            binding.btnLogin.isEnabled = false
            binding.btnRegister.isEnabled = false
            // Optionally, apply a semi-transparent overlay to visually indicate that the views are disabled
            binding.layoutLogin.alpha = 0.5f
            // Show the loading layout
            binding.loadingLayout.root.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        activity?.runOnUiThread {
            // Enable user interaction for all views in the main layout
            binding.inputUsername.isEnabled = true
            binding.inputPassword.isEnabled = true
            binding.btnLogin.isEnabled = true
            binding.btnRegister.isEnabled = true
            // Restore the alpha of the main layout
            binding.layoutLogin.alpha = 1.0f
            // Hide the loading layout
            binding.loadingLayout.root.visibility = View.GONE
        }
    }
}