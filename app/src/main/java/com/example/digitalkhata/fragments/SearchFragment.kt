package com.example.digitalkhata.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitalkhata.R
import com.example.digitalkhata.api.RetrofitClient
import com.example.digitalkhata.databinding.FragmentDashboardBinding
import com.example.digitalkhata.databinding.FragmentSearchBinding
import com.example.digitalkhata.model.UserAdapter
import com.example.digitalkhata.model.UserResponse
import com.example.digitalkhata.util.LocalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var adapter: UserAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.viewRecycler.setHasFixedSize(true)
        binding.viewRecycler.layoutManager = LinearLayoutManager(requireContext())


        adapter = UserAdapter(emptyList()) { user -> onSearchedUserClick(user) }
        binding.viewRecycler.adapter = adapter

        setupListeners()

        return binding.root
    }

    private fun onSearchedUserClick(user: UserResponse) {
        // have to implement logic
        Toast.makeText(requireContext(), "Clicked ${user.fullname}", Toast.LENGTH_SHORT).show()
    }

    private fun setupListeners() {
        binding.viewSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            @SuppressLint("SetTextI18n")
            override fun onQueryTextChange(newText: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    activity?.runOnUiThread { binding.viewEmpty.text = "Searching ..." }
                    val users = search(newText.toString())
                    activity?.runOnUiThread {
                        binding.viewEmpty.text = "No search result"
                        adapter.setFilteredList(users)
                        if (adapter.itemCount < 1) {
                            binding.viewRecycler.visibility = View.GONE
                            binding.viewEmpty.visibility = View.VISIBLE
                        } else {
                            binding.viewRecycler.visibility = View.VISIBLE
                            binding.viewEmpty.visibility = View.GONE
                        }
                    }
                }
                return true
            }

        })
    }

    private suspend fun search(query: String) : List<UserResponse> {
        try {
            val token = LocalStorage.getAuthToken(requireContext())
            val response = RetrofitClient.apiService.searchUser("Bearer $token", query)
            if (response.isSuccessful) {
                val users = response.body()?.data
                if(users != null)
                    return users.filter { it.userId != LocalStorage.getUserId(requireContext())?.toInt() }
            } else {
                // Handle unsuccessful response here
                //showToast("Failed to search users: ${response.message()}")
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

    private fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

}