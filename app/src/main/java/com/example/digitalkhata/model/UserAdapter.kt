package com.example.digitalkhata.model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.digitalkhata.R

class UserAdapter(private var userList: List<UserResponse>, private val onItemClick: (UserResponse) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        fun bind(item: UserResponse){
            itemView.setOnClickListener{onItemClick(item)}
        }
        val username: TextView = itemView.findViewById(R.id.title_username)
        val fullname: TextView = itemView.findViewById(R.id.title_fullname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.username.text = userList[position].username
        holder.fullname.text = userList[position].fullname
        holder.bind(userList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(list: List<UserResponse>)
    {
        this.userList = list
        notifyDataSetChanged()
    }
}