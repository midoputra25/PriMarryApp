package com.example.primarryapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.primarryapp.databinding.ActivityChat2Binding
import com.example.primarryapp.model.Message
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database

class ActivityChat2 : AppCompatActivity() {

    private lateinit var binding: ActivityChat2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: FirebaseMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChat2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            db = Firebase.database
            val messagesRef = db.reference.child(MainActivity.MESSAGES_CHILD)

            fetchCurrentUser { currentUser ->
                binding.sendButton.setOnClickListener {
                    val friendlyMessage = Message(
                        binding.messageEditText.text.toString(),
                        currentUser.displayName.toString(),
                        currentUser.photoUrl.toString(),
                        System.currentTimeMillis()
                    )
                    messagesRef.push().setValue(friendlyMessage) { error, _ ->
                        if (error != null) {
                            Toast.makeText(
                                this,
                                getString(R.string.send_error) + error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.send_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    binding.messageEditText.setText("")
                }

                val manager = LinearLayoutManager(this)
                manager.stackFromEnd = true
                binding.messageRecyclerView.layoutManager = manager

                val options = FirebaseRecyclerOptions.Builder<Message>()
                    .setQuery(messagesRef, Message::class.java)
                    .build()
                adapter = FirebaseMessageAdapter(options, currentUser.displayName)
                binding.messageRecyclerView.adapter = adapter
            }
        }
    }

    private fun fetchCurrentUser(callback: (FirebaseUser) -> Unit) {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            callback(firebaseUser)
        } else {
        }
    }

    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }
}
