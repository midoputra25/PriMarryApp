package com.example.primarryapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String = "",
    val authorId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = 0,
    var authorName: String = "",
    var comments: MutableList<Comment> = mutableListOf()
) : Parcelable


@Parcelize
data class Comment(
    var commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val timestamp: Long = 0
) : Parcelable





