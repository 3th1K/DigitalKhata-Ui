package com.example.digitalkhata.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.digitalkhata.R
import com.example.digitalkhata.api.ApiService
import com.example.digitalkhata.api.RetrofitClient
import com.example.digitalkhata.databinding.FragmentLoginBinding
import com.example.digitalkhata.databinding.FragmentRegisterBinding
import com.example.digitalkhata.model.UserRegisterRequest
import com.example.digitalkhata.model.UserResponse
import com.example.digitalkhata.util.ErrorResolver
import com.example.digitalkhata.util.LocalStorage
import com.example.digitalkhata.util.TokenService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.btnRegister.setOnClickListener {
            handleRegistration()
        }

        return binding.root
    }

    private fun handleRegistration()
    {
        val fullname = binding.inputFullname.text.toString()
        val username = binding.inputUsername.text.toString()
        val email = binding.inputEmail.text.toString()
        val setPassword = binding.inputSetPassword.text.toString()

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

    private fun validateInputs() : Boolean
    {
        var isValid  = true
        if(binding.inputFullname.text.isEmpty())
        {
            binding.inputFullname.error = "Please enter your full name"
            isValid = false
        }
        if(binding.inputUsername.text.isEmpty())
        {
            binding.inputUsername.error = "Please enter your username"
            isValid = false
        }
        if(binding.inputEmail.text.isEmpty())
        {
            binding.inputEmail.error = "Please enter your email"
            isValid = false
        }
        if(binding.inputSetPassword.text.isEmpty())
        {
            binding.inputSetPassword.error = "Please enter the password"
            isValid = false
        }
        if(binding.inputRewritePassword.text.isEmpty())
        {
            binding.inputRewritePassword.error = "Please rewrite the same password"
            isValid = false
        }
        if(binding.inputSetPassword.text.toString() != binding.inputRewritePassword.text.toString())
        {
            binding.inputRewritePassword.error = "Passwords does not matches"
            isValid = false
        }
        return isValid
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
                        LocalStorage.saveAuthToken(requireContext(), token)
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
                        activity?.runOnUiThread{
                            binding.inputUsername.error = "This username is not available"
                            binding.inputEmail.error = "User already registered with this email"
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

    private fun navigateToDashboard() {
        activity?.runOnUiThread{
            findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
        }
    }



    private fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun showLoading() {
        activity?.runOnUiThread {
            // Disable user interaction for all views in the main layout
            binding.inputUsername.isEnabled = false
            binding.inputFullname.isEnabled = false
            binding.inputSetPassword.isEnabled = false
            binding.inputRewritePassword.isEnabled = false
            binding.btnRegister.isEnabled = false
            // Optionally, apply a semi-transparent overlay to visually indicate that the views are disabled
            binding.layoutRegister.alpha = 0.5f
            // Show the loading layout
            binding.loadingLayout.root.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        activity?.runOnUiThread {
            // Enable user interaction for all views in the main layout
            binding.inputUsername.isEnabled = true
            binding.inputFullname.isEnabled = true
            binding.inputSetPassword.isEnabled = true
            binding.inputRewritePassword.isEnabled = true
            binding.btnRegister.isEnabled = true
            // Restore the alpha of the main layout
            binding.layoutRegister.alpha = 1.0f
            // Hide the loading layout
            binding.loadingLayout.root.visibility = View.GONE
        }
    }
    private fun handleRegisterError(e: Exception) {
        showToast("Registration failed, try again later. ${e.message}")
    }

}