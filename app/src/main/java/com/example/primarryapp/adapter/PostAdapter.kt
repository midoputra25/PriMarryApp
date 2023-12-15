package com.example.primarryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.primarryapp.R
import com.example.primarryapp.model.Post

class PostAdapter(
    private val posts: List<Post>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val authorNameTextView: TextView = itemView.findViewById(R.id.authorNameTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        private val commentButton: Button = itemView.findViewById(R.id.commentButton)

        init {
            // Set up click listener in the ViewHolder's init block
            commentButton.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(post: Post) {
            titleTextView.text = post.title
            authorNameTextView.text = "Written By: " + post.authorName
            contentTextView.text = post.content
        }
    }
}
