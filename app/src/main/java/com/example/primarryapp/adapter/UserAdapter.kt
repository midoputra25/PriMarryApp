package com.example.primarryapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.primarryapp.ActivityChat2
import com.example.primarryapp.model.User
import com.example.primarryapp.R

class UserAdapter(private val userList: List<User>, private val context: Context) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // ViewHolder for each user item
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val temp: TextView = itemView.findViewById(R.id.temp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.userName.text = currentUser.name
        holder.temp.text = currentUser.temp

        // Set click listener for each item
        holder.itemView.setOnClickListener {
            // When an item is clicked, navigate to ActivityChat2
            val intent = Intent(context, ActivityChat2::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}