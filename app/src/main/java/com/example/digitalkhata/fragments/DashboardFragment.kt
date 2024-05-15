package com.example.digitalkhata.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitalkhata.R
import com.example.digitalkhata.api.RetrofitClient
import com.example.digitalkhata.databinding.FragmentDashboardBinding
import com.example.digitalkhata.model.UserAdapter
import com.example.digitalkhata.model.UserResponse
import com.example.digitalkhata.util.LocalStorage
import com.example.digitalkhata.util.TokenService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    private lateinit var adapter: UserAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        initializeUserExpenses()
        setupUI()
        setupClickListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        checkLoggedIn()
    }
    private fun checkLoggedIn()
    {
        if(!TokenService.isUserLoggedIn(requireContext())) {
            showToast("User session ended, please login again")
            LocalStorage.clearAllData(requireContext())
            activity?.runOnUiThread {
                findNavController().navigate(R.id.action_dashboardFragment_to_loginFragment)
            }
        }
    }

    private fun initializeUserExpenses()
    {
        binding.viewRecycler.setHasFixedSize(true)
        binding.viewRecycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = UserAdapter(emptyList()) { user -> onExpenseUserClick(user) }
        binding.viewRecycler.adapter = adapter

        updateUserExpenses()

    }

    private fun updateUserExpenses() {
        showLoading()
        CoroutineScope(Dispatchers.IO).launch{
            val userId = LocalStorage.getUserId(requireContext())
            if(!userId.isNullOrEmpty())
            {
                val users = fetchUserExpenses(userId.toInt())
                activity?.runOnUiThread{
                    adapter.setFilteredList(users)
                    hideLoading()
                }
            }
        }
    }

    private suspend fun fetchUserExpenses(id: Int) : List<UserResponse> {
        try {
            val token = LocalStorage.getAuthToken(requireContext())
            val response = RetrofitClient.apiService.getUserExpenses("Bearer $token", id)
            if (response.isSuccessful)
            {
                val users = response.body()?.data
                if(users != null)
                    return users
            }
            else if(response.code()==401)
            {
                checkLoggedIn()
            }
        } catch (e: HttpException) {
            // Handle HTTP exception
            showToast("HTTP Exception: ${e.message()}")
        } catch (e: Exception) {
            // Handle other exceptions
            showToast("Exception: ${e.message}")
        }
        return emptyList()
    }
    private fun onExpenseUserClick(user: UserResponse) {
        // have to implement logic
        val action = DashboardFragmentDirections.actionDashboardFragmentToExpenseFragment(user)
        findNavController().navigate(action)
    }

    private fun setupUI() {
        setupProfile()

        // Set up drawer toggle
        val toggle = ActionBarDrawerToggle(
            requireActivity(), binding.layoutDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.layoutDrawer.addDrawerListener(toggle)
        toggle.syncState()
    }
    private fun setupProfile()
    {
        binding.textUsername.text = LocalStorage.getUserName(requireContext())
        binding.textFullname.text = LocalStorage.getFullName(requireContext())
        binding.textEmail.text = LocalStorage.getEmail(requireContext())
    }

    private fun setupClickListeners()
    {
        // Set up click listener for user expenses
        binding.btnRefresh.setOnClickListener{
            updateUserExpenses()
        }
        // Set up click listener for search image to navigate to search
        binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_searchFragment)
        }

        // Set up click listener for profile image to open the drawer
        binding.imageProfile.setOnClickListener {
            binding.layoutDrawer.openDrawer(GravityCompat.END) // Open drawer from right
        }

        // Set up click listener for logout button
        binding.btnLogout.setOnClickListener{
            LocalStorage.clearAllData(requireContext())
            findNavController().navigate(R.id.action_dashboardFragment_to_loginFragment)
        }

        binding.swiperefresh.setOnRefreshListener{
            updateUserExpenses()
            binding.swiperefresh.isRefreshing = false;
        }
    }

    private fun showLoading() {
        activity?.runOnUiThread {
            // Disable user interaction for all views in the main layout
            binding.imageSearch.isEnabled = false
            binding.myImage.isEnabled = false
            // Optionally, apply a semi-transparent overlay to visually indicate that the views are disabled
            binding.layoutDashboard.alpha = 0.5f
            // Show the loading layout
            binding.loadingLayout.root.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        activity?.runOnUiThread {
            // Enable user interaction for all views in the main layout
            binding.imageSearch.isEnabled = true
            binding.myImage.isEnabled = true
            // Restore the alpha of the main layout
            binding.layoutDashboard.alpha = 1.0f
            // Hide the loading layout
            binding.loadingLayout.root.visibility = View.GONE
        }
    }
    private fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}