package com.example.digitalkhata.model

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalkhata.R

class ExpenseAdapter(
    private var userId: Int,
    private var expenseList: List<ExpenseResponse>,
    private val onItemClick: (ExpenseResponse) -> Unit,
    private val onDeleteClick: (ExpenseResponse) -> Unit // Listener for delete button click
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeWithDelete = 1
    private val viewTypeNormal = 2

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ExpenseResponse) {
            itemView.setOnClickListener { onItemClick(item) }
        }

        val expenseConstLayout: ConstraintLayout = itemView.findViewById(R.id.expense_constLayout)
        val amount: TextView = itemView.findViewById(R.id.text_amount)
        val description: TextView = itemView.findViewById(R.id.text_description)
        val date: TextView = itemView.findViewById(R.id.text_date)
    }

    inner class ExpenseViewHolderWithDelete(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ExpenseResponse) {
            itemView.setOnClickListener { onItemClick(item) }
            itemView.findViewById<View>(R.id.btn_delete).setOnClickListener {
                // Call the onDeleteClick listener passing the position to delete
                onDeleteClick(item)
            }
        }

        val expenseConstLayout: ConstraintLayout = itemView.findViewById(R.id.expense_constLayout)
        val amount: TextView = itemView.findViewById(R.id.text_amount)
        val description: TextView = itemView.findViewById(R.id.text_description)
        val date: TextView = itemView.findViewById(R.id.text_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypeWithDelete -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.expense_item_with_delete, parent, false)
                ExpenseViewHolderWithDelete(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.each_expense_item, parent, false)
                ExpenseViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = expenseList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val payerId = expenseList[position].payerUserId
        val payeeId = expenseList[position].payeeUserId
        val amount = expenseList[position].amount

        if (holder is ExpenseViewHolderWithDelete) {
            holder.description.text = expenseList[position].description
            if (payeeId == userId) {
                holder.amount.text = "+ $amount"
                holder.expenseConstLayout.setBackgroundColor(Color.GREEN)
            } else if (payerId == userId) {
                holder.amount.text = "- $amount"
                holder.expenseConstLayout.setBackgroundColor(Color.RED)
            }
            holder.date.text = expenseList[position].localDateTime
            holder.bind(expenseList[position])
        } else if (holder is ExpenseViewHolder) {
            holder.description.text = expenseList[position].description
            if (payeeId == userId) {
                holder.amount.text = "+ $amount"
                holder.expenseConstLayout.setBackgroundColor(Color.GREEN)
            } else if (payerId == userId) {
                holder.amount.text = "- $amount"
                holder.expenseConstLayout.setBackgroundColor(Color.RED)
            }
            holder.date.text = expenseList[position].localDateTime
            holder.bind(expenseList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        val payerId = expenseList[0].payerUserId
        return if (position == 0 && payerId == userId) viewTypeWithDelete else viewTypeNormal
    }

    fun setExpenseList(list: List<ExpenseResponse>) {
        this.expenseList = list
        notifyDataSetChanged()
    }
}
