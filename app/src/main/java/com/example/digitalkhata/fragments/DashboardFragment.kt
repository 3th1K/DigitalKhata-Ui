package com.example.digitalkhata.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.example.digitalkhata.R
import com.example.digitalkhata.databinding.FragmentDashboardBinding
import com.example.digitalkhata.databinding.FragmentLoginBinding
import com.example.digitalkhata.util.LocalStorage

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        setupUI()
        setupClickListeners()

        return binding.root
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
    }
}