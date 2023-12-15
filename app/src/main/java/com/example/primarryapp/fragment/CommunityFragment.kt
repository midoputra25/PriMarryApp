package com.example.primarryapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primarryapp.R
import com.example.primarryapp.adapter.PostAdapter
import com.example.primarryapp.model.Post
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class CommunityFragment : Fragment() {

    private val TAG = "CommunityFragment"
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var postRecyclerView: RecyclerView
    private lateinit var createPostButton: Button

    private val postList = mutableListOf<Post>()
    private lateinit var postAdapter: PostAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community, container, false)



        postRecyclerView = view.findViewById(R.id.postsRecyclerView)
        createPostButton = view.findViewById(R.id.postButton)
        progressBar = view.findViewById(R.id.progressBar)

        postAdapter = PostAdapter(postList) { position ->
            // Handle item click, e.g., open post detail screen or dialog
            val selectedPost = postList[position]
            showPostDetail(selectedPost)
        }

        postRecyclerView.adapter = postAdapter
        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        

        // Set up Firebase and fetch posts
        fetchPosts()

        createPostButton.setOnClickListener {
            // Open a new screen or dialog to allow users to create a new post
            showCreatePostDialog()
        }

        return view
    }

    private fun fetchPosts() {
        showLoading(true)
        coroutineScope.launch {
            val db = FirebaseFirestore.getInstance()
            try {
                val result = withContext(Dispatchers.IO) {
                    db.collection("posts")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .await()
                }

                val fetchedPosts = result.documents.mapNotNull { document ->
                    val post = document.toObject(Post::class.java)
                    post?.let {
                        it.authorName = fetchAuthorName(it.authorId)
                    }
                    post
                }

                withContext(Dispatchers.Main) {
                    postList.clear()
                    postList.addAll(fetchedPosts)
                    postAdapter.notifyDataSetChanged()
                    showLoading(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching posts: ", e)
            }
        }
    }

    private suspend fun fetchAuthorName(authorId: String): String {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("users")
                .document(authorId)
                .get()
                .await()

            if (document.exists()) {
                document.getString("userName") ?: "Unknown Author"
            } else {
                "Unknown Author"
            }
        } catch (e: Exception) {
            "Unknown Author"
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun fetchUserName(userId: String, callback: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("userName") ?: "Unknown Author"
                    Log.d(TAG, "Fetched user name: $userName")
                    callback(userName)
                } else {
                    Log.d(TAG, "No such document for user ID: $userId")
                    callback("Unknown Author")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching user document", exception)
                callback("Unknown Author")
            }
    }

    private fun showPostDetail(post: Post) {
        val postDetailFragment = PostDetailFragment()
        val bundle = Bundle().apply {
            putParcelable("post", post)
        }
        postDetailFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, postDetailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showCreatePostDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_post, null)
        val editTextPostTitle = dialogView.findViewById<EditText>(R.id.editTextPostTitle)
        val editTextPostContent = dialogView.findViewById<EditText>(R.id.editTextPostContent)
        val contentTextInputLayout = dialogView.findViewById<TextInputLayout>(R.id.contentTextInputLayout)

        editTextPostContent?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val remainingChars = 1000 - (s?.length ?: 0) // Change 500 to your desired character limit
                contentTextInputLayout?.helperText = "Characters remaining: $remainingChars"
            }
        })

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("New Post")
            .setPositiveButton("Post") { _, _ ->
                val postTitle = editTextPostTitle?.text.toString()
                val postContent = editTextPostContent?.text.toString()
                if (!postTitle.isNullOrEmpty() && !postContent.isNullOrEmpty()) {
                    createNewPost(postTitle, postContent)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createNewPost(title: String, content: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val authorId = currentUser?.uid ?: "anonymous"

        fetchUserName(authorId) { authorName ->
            val newPost = Post(
                id = UUID.randomUUID().toString(),
                authorId = authorId,
                authorName = authorName,
                title = title,
                content = content,
                timestamp = System.currentTimeMillis()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("posts")
                .add(newPost)
                .addOnSuccessListener {
                    fetchPosts()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding post", e)
                }
        }
    }




}
