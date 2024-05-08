package com.example.digitalkhata.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.digitalkhata.databinding.FragmentAddExpenseBinding

class AddExpenseFragment : DialogFragment() {

    private lateinit var binding: FragmentAddExpenseBinding
    // Define a callback interface
    interface OnExpenseAddedListener {
        fun onExpenseAdded(amount: Double, description: String)
    }

    // Declare a listener variable
    private var listener: OnExpenseAddedListener? = null

    // Method to set the listener
    fun setOnExpenseAddedListener(listener: OnExpenseAddedListener) {
        this.listener = listener
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false)

        binding.buttonCreateExpense.setOnClickListener{
            if(validateInputs()){
                addExpenseAndDismiss(binding.editTextAmount.text.toString().toDouble(), binding.editTextDescription.text.toString())
            }
        }

        return binding.root
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val amountString = binding.editTextAmount.text.toString()
        if (amountString.isEmpty()) {
            isValid = false
            binding.editTextAmount.error = "Please enter an amount"
        } else if (amountString.toDouble() < 1) {
            isValid = false
            binding.editTextAmount.error = "Minimum allowed amount is 1"
        }

        val description = binding.editTextDescription.text.toString()
        if (description.isEmpty()) {
            isValid = false
            binding.editTextDescription.error = "Please add suitable description for the expense"
        }
        return isValid
    }

    private fun addExpenseAndDismiss(amount: Double, description: String) {
        // Notify listener and pass the amount and description
        listener?.onExpenseAdded(amount, description)

        // Dismiss the dialog
        dismiss()
    }

}