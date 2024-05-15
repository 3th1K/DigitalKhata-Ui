package com.example.digitalkhata.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitalkhata.R
import com.example.digitalkhata.api.RetrofitClient
import com.example.digitalkhata.databinding.FragmentDashboardBinding
import com.example.digitalkhata.databinding.FragmentExpenseBinding
import com.example.digitalkhata.model.ExpenseAdapter
import com.example.digitalkhata.model.ExpenseAddRequest
import com.example.digitalkhata.model.ExpenseResponse
import com.example.digitalkhata.model.UserAdapter
import com.example.digitalkhata.model.UserResponse
import com.example.digitalkhata.model.UserTransactionHistory
import com.example.digitalkhata.util.LocalStorage
import com.example.digitalkhata.util.TokenService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException


class ExpenseFragment : Fragment(), AddExpenseFragment.OnExpenseAddedListener {

    private val args: ExpenseFragmentArgs by navArgs()

    private lateinit var binding: FragmentExpenseBinding
    private lateinit var adapter: ExpenseAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExpenseBinding.inflate(inflater, container, false)

        binding.btnAddExpense.setOnClickListener {
            showAddExpenseDialog()
        }
        binding.swiperefresh.setOnRefreshListener {
            updateTransactionHistory()
            binding.swiperefresh.isRefreshing = false;
        }


        binding.textUsername.text = args.otherUser.fullname

        binding.viewRecycler.setHasFixedSize(true)
        binding.viewRecycler.layoutManager = LinearLayoutManager(requireContext())

        val userId = LocalStorage.getUserId(requireContext())
        if (!userId.isNullOrEmpty()) {
            adapter = ExpenseAdapter(
                userId.toInt(),
                emptyList(),
                onItemClick = {
                    // Handle item click here
                },
                onDeleteClick = { expense ->
                    // Handle delete button click here
                    CoroutineScope(Dispatchers.IO).launch {
                        deleteExpense(expense)
                        updateTransactionHistory()
                    }
                }
            )
        }
        binding.viewRecycler.adapter = adapter

        updateTransactionHistory()


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
                findNavController().navigate(R.id.action_expenseFragment_to_loginFragment)
            }
        }
    }

    private suspend fun deleteExpense(expense: ExpenseResponse) {
        try {
            val token = LocalStorage.getAuthToken(requireContext())
            val response =
                RetrofitClient.apiService.deleteExpense("Bearer $token", expense.expenseId)
            if (response.isSuccessful) {
                showToast("Expense deleted")
            }
            else if(response.code()==401)
            {
                checkLoggedIn()
            }
            else{
                showToast("Expense was not deleted, please try again later")
            }
        } catch (e: HttpException) {
            // Handle HTTP exception
            showToast("HTTP Exception: ${e.message()}")
        } catch (e: Exception) {
            // Handle other exceptions
            showToast("Exception: ${e.message}")
        }
    }

    private fun updateTransactionHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = LocalStorage.getUserId(requireContext())
            if (!userId.isNullOrEmpty()) {
                val transactionHistory =
                    fetchTransactionHistory(userId.toInt(), args.otherUser.userId)
                activity?.runOnUiThread {
                    if (transactionHistory != null) {
                        val transactions = transactionHistory.transactions
                        updateTotal(transactionHistory)
                        adapter.setExpenseList(transactions.reversed())
                    } else {
                        adapter.setExpenseList(emptyList())
                    }
                }

            } else {
                showToast("Error : cannot get local user")
            }
        }
    }

    private fun updateTotal(transactionHistory: UserTransactionHistory) {
        activity?.runOnUiThread {
            val totalTransactionAmount = transactionHistory.transactions.sumOf { it.amount }
            binding.textTotalTransactionAmount.text = "Total transactions ₹$totalTransactionAmount"

            val userId = LocalStorage.getUserId(requireContext())
            if (!userId.isNullOrEmpty()) {
                val x: Double? = transactionHistory.netAmountOwedOrReceived[userId.toInt()]
                if (x != null) {
                    if (x < 0) {
                        binding.textAmountToBePaidReceived.text =
                            "Amount yet to be received: ₹${-x}"
                    } else {
                        binding.textAmountToBePaidReceived.text = "Amount to be Paid: ₹$x"
                    }
                }

            } else {
                showToast("Error : cannot get local user")
            }
        }
    }

    private suspend fun addExpense(request: ExpenseAddRequest): ExpenseResponse? {
        try {
            val token = LocalStorage.getAuthToken(requireContext())
            val response = RetrofitClient.apiService.addExpense("Bearer $token", request)
            if (response.isSuccessful) {
                return response.body()?.data
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
        return null
    }

    private suspend fun fetchTransactionHistory(id: Int, otherId: Int): UserTransactionHistory? {
        try {
            val token = LocalStorage.getAuthToken(requireContext())
            val response =
                RetrofitClient.apiService.getTransactionHistory("Bearer $token", id, otherId)
            if (response.isSuccessful) {
                return response.body()?.data
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
        return null
    }

    private fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onExpenseAdded(amount: Double, description: String) {
        val userId = LocalStorage.getUserId(requireContext())
        if (!userId.isNullOrEmpty()) {
            var expenseRequest =
                ExpenseAddRequest(userId.toInt(), args.otherUser.userId, amount, description)
            CoroutineScope(Dispatchers.IO).launch {
                var expense = addExpense(expenseRequest)
                if (expense != null) {
                    updateTransactionHistory()
                    showToast("Expense added successfully")
                } else {
                    showToast("Expense was not added, try again later")
                }
            }

        } else {
            showToast("Error : cannot get local user")
        }
    }

    // Where you show the AddExpenseFragment dialog
    private fun showAddExpenseDialog() {
        val addExpenseFragment = AddExpenseFragment()

        // Set the listener
        addExpenseFragment.setOnExpenseAddedListener(this)

        // Show the dialog
        addExpenseFragment.show(
            (activity as AppCompatActivity).supportFragmentManager,
            "addExpenseFragment"
        )
    }


}