package com.example.primarryapp

import com.google.firebase.auth.FirebaseAuth

object AuthenticationHelper {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}