package com.example.digitalkhata.model

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalkhata.R
import com.example.digitalkhata.util.LocalStorage
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class ExpenseAdapter(private var userId: Int, private var expenseList: List<ExpenseResponse>, private val onItemClick: (ExpenseResponse) -> Unit)  : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>()
{
    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        fun bind(item: ExpenseResponse){
            itemView.setOnClickListener{onItemClick(item)}
        }
        val expenseConstLayout: ConstraintLayout = itemView.findViewById(R.id .expense_constLayout)
        val amount: TextView = itemView.findViewById(R.id.text_amount)
        val description: TextView = itemView.findViewById(R.id.text_description)
        val date: TextView = itemView.findViewById(R.id.text_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val payerId = expenseList[position].payerUserId
        val payeeId = expenseList[position].payeeUserId
        val amount = expenseList[position].amount
        if(payeeId == userId)
        {
            holder.amount.text = "+ $amount"
            holder.expenseConstLayout.setBackgroundColor(Color.GREEN)
        }
        else if(payerId == userId)
        {
            holder.amount.text = "- $amount"
            holder.expenseConstLayout.setBackgroundColor(Color.RED)
        }

        holder.description.text = expenseList[position].description

        val date = expenseList[position].date
        val localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("dd MMM hh.mm a", Locale.ENGLISH)

        holder.date.text = formatter.format(localDateTime)


        holder.bind(expenseList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setExpenseList(list: List<ExpenseResponse>)
    {
        this.expenseList = list
        notifyDataSetChanged()
    }
}