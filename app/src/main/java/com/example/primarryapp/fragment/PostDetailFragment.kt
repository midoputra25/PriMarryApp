package com.example.primarryapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primarryapp.R
import com.example.primarryapp.adapter.CommentAdapter
import com.example.primarryapp.model.Comment
import com.example.primarryapp.model.Post

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class PostDetailFragment : Fragment() {

    private lateinit var postTitleTextView: TextView
    private lateinit var postContentTextView: TextView
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private var post: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        postTitleTextView = view.findViewById(R.id.postTitleTextView)
        postContentTextView = view.findViewById(R.id.postContentTextView)
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView)


        arguments?.let {
            post = it.getParcelable("post")
        }

        if (post != null) {
            Log.d("PostDetailFragment", "Received Post: $post")

            postTitleTextView.text = post!!.title
            postContentTextView.text = post!!.content

            commentAdapter = CommentAdapter(post!!.comments)
            commentsRecyclerView.adapter = commentAdapter
            commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        } else {
            Log.e("PostDetailFragment", "No post data received")

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val commentEditText: EditText = view.findViewById(R.id.commentEditText)
        val postCommentButton: Button = view.findViewById(R.id.postCommentButton)
        val deletePostButton: Button = view.findViewById(R.id.deletePostButton)

        postCommentButton.setOnClickListener {
            val commentContent = commentEditText.text.toString().trim()
            if (commentContent.isNotEmpty()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                currentUser?.let { user ->
                    val newComment = Comment(
                        userId = user.uid,
                        userName = user.displayName ?: "Unknown User",
                        content = commentContent,
                        timestamp = System.currentTimeMillis()
                    )
                    saveCommentToFirestore(newComment)
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }

        deletePostButton.setOnClickListener {
            deletePost()
        }

        fetchCommentsFromFirestore()
    }

    private fun saveCommentToFirestore(comment: Comment) {
        post?.let { post ->
            val postId = post.id
            val firestore = FirebaseFirestore.getInstance()

            // Reference to the post document
            val postRef = firestore.collection("posts").document(postId)

            // Reference to the comments subcollection of the post document
            val commentsRef = postRef.collection("comments")

            // Generate a unique commentId (e.g., using UUID)
            val commentId = UUID.randomUUID().toString()

            // Set the commentId field
            comment.commentId = commentId

            commentsRef.document(commentId) // Use commentId as the document ID
                .set(comment)
                .addOnSuccessListener {
                    // Comment added successfully
                    Log.d("PostDetailFragment", "Comment added successfully")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to post comment: $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun fetchCommentsFromFirestore() {
        post?.let { post ->
            val postId = post.id
            val firestore = FirebaseFirestore.getInstance()

            // Reference to the post document
            val postRef = firestore.collection("posts").document(postId)

            // Reference to the comments subcollection of the post document
            val commentsRef = postRef.collection("comments")

            commentsRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("PostDetailFragment", "Failed to read comments", e)
                    return@addSnapshotListener
                }

                val comments = mutableListOf<Comment>()
                if (snapshot != null) {
                    for (commentSnapshot in snapshot) {
                        val comment = commentSnapshot.toObject(Comment::class.java)
                        comments.add(comment)
                    }
                }
                commentAdapter.updateComments(comments)
            }
        }
    }

    private fun fetchPostsFromFirestore() {
        // Assuming you have a reference to the Firestore collection for posts
        val firestore = FirebaseFirestore.getInstance()
        val postsRef = firestore.collection("posts")

        // Add a snapshot listener to fetch posts
        postsRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("PostDetailFragment", "Failed to read posts", e)
                return@addSnapshotListener
            }

            // Update your UI or handle the fetched posts as needed
            // For example, if you are using a RecyclerView adapter for posts, update the data
            // adapter.updatePosts(posts)
        }
    }

    private fun deletePost() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        post?.let { post ->
            val postUserId = post.authorId

            Log.d(
                "PostDetailFragment",
                "Post owner ID: $postUserId, Current user ID: ${currentUser?.uid}"
            )

            val postId = post.id
            Log.d("PostDetailFragment", "Post ID to delete: $postId")

            if (currentUser != null && currentUser.uid == postUserId) {
                val firestore = FirebaseFirestore.getInstance()

                // Reference to the post document
                val postRef = firestore.collection("posts").document(postId)

                // Reference to the comments subcollection of the post document
                val commentsRef = postRef.collection("comments")

                // Delete the comments first
                commentsRef.get().addOnSuccessListener { commentsSnapshot ->
                    val batch = firestore.batch()

                    for (commentDocument in commentsSnapshot) {
                        val commentDocRef = commentsRef.document(commentDocument.id)
                        batch.delete(commentDocRef)
                    }

                    // Commit the batch to delete comments
                    batch.commit().addOnSuccessListener {
                        // Comments deleted successfully
                        Log.d("PostDetailFragment", "Comments deleted successfully")

                        // Now, delete the post
                        postRef.delete().addOnSuccessListener {
                            // Deletion successful
                            Log.d("PostDetailFragment", "Post deleted successfully")
                            postRef.delete()
                            // Fetch updated posts after deletion
                            fetchPostsFromFirestore()

                            // Remove current fragment from the back stack
                            parentFragmentManager.popBackStack()
                        }.addOnFailureListener { e ->
                            // Failed to delete post
                            Log.e("PostDetailFragment", "Failed to delete post: $e")
                            Toast.makeText(
                                requireContext(),
                                "Failed to delete post: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener { e ->
                        // Failed to delete comments
                        Log.e("PostDetailFragment", "Failed to delete comments: $e")
                        Toast.makeText(
                            requireContext(),
                            "Failed to delete comments: $e",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                // User is not the owner of the post, show an error or handle as needed
                Toast.makeText(
                    requireContext(),
                    "You don't have permission to delete this post",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}

