package com.example.primarryapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primarryapp.R
import com.example.primarryapp.adapter.UserAdapter
import com.example.primarryapp.model.User

class UsersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Fetch user data (dummy data for demonstration)
        val userList: List<User> = fetchUserList()

        // Initialize and set up the adapter
        val userAdapter = UserAdapter(userList, requireContext())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }

        return view
    }

    // Dummy function to simulate fetching user data
    private fun fetchUserList(): List<User> {
        // Replace this with actual data fetching logic from your data source
        return listOf(
            User("User 1", "Fajrin Trisnaramawati", "M.Psi., Psikolog")
            // Add more users as needed
        )
    }
}
