package com.example.digitalkhata.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.digitalkhata.R
import com.example.digitalkhata.util.LocalStorage
import javax.security.auth.login.LoginException


class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var profileImage: ImageView
    private lateinit var userNameText: TextView
    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setupUI()
        setupClickListeners()
    }
    private fun setupUI()
    {
        enableEdgeToEdge()
        setContentView(R.layout.dashboard_ui)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_ui))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        profileImage = findViewById(R.id.profile_image)

        userNameText = findViewById(R.id.text_username)
        fullNameText = findViewById(R.id.text_fullname)
        emailText = findViewById(R.id.text_email)

        setupProfile()

        // Set up drawer toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupProfile()
    {
        userNameText.text = LocalStorage.getUserName(this@DashboardActivity)
        fullNameText.text = LocalStorage.getFullName(this@DashboardActivity)
        emailText.text = LocalStorage.getEmail(this@DashboardActivity)
    }

    private fun setupClickListeners()
    {
        // Set up click listener for profile image to open the drawer
        profileImage.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END) // Open drawer from right
        }

        // Set up click listener for logout button
        val logoutButton = findViewById<Button>(R.id.btn_logout)
        logoutButton.setOnClickListener{
            handleLogout()
        }
    }

    private fun handleLogout()
    {
        LocalStorage.clearAllData(this@DashboardActivity)
        navigateToLogin()
    }
    private fun navigateToLogin()
    {
        val intent = Intent(this@DashboardActivity, MainActivity::class.java)
        startActivity(intent)
    }
    private fun Context.showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}