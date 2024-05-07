package com.example.digitalkhata.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalkhata.R
import com.example.digitalkhata.api.RetrofitClient
import com.example.digitalkhata.model.UserAdapter
import com.example.digitalkhata.model.UserResponse
import com.example.digitalkhata.util.LocalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var emptyView: TextView

    private lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupListeners() {

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            @SuppressLint("SetTextI18n")
            override fun onQueryTextChange(newText: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch{
                    runOnUiThread{emptyView.text = "Searching ..."}
                    val users = search(newText.toString())
                    runOnUiThread{
                        emptyView.text = "No search result"
                        adapter.setFilteredList(users)
                        if(adapter.itemCount<1)
                        {
                            recyclerView.visibility = View.GONE
                            emptyView.visibility = View.VISIBLE
                        }
                        else
                        {
                            recyclerView.visibility = View.VISIBLE
                            emptyView.visibility = View.GONE
                        }
                    }
                }
                return true
            }

        })

    }

    private fun onSearchedUserClick(user:UserResponse){
        Toast.makeText(this, "Clicked ${user.fullname}", Toast.LENGTH_SHORT).show()
    }

    private suspend fun search(query: String) : List<UserResponse> {
        try {
            val token = LocalStorage.getAuthToken(this@SearchActivity)
            val response = RetrofitClient.apiService.searchUser("Bearer $token", query)
            if (response.isSuccessful) {
                val users = response.body()?.data
                if(users != null)
                    return users.filter { it.userId != LocalStorage.getUserId(this@SearchActivity)?.toInt() }
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

    private fun setupUI() {
        setContentView(R.layout.search_ui)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_search))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.view_recycler)
        searchView = findViewById(R.id.view_search)
        emptyView = findViewById(R.id.view_empty)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)


        adapter = UserAdapter(emptyList()){user -> onSearchedUserClick(user)}
        recyclerView.adapter = adapter
    }

    private fun Context.showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}