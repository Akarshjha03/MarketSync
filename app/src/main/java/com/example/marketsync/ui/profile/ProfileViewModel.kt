package com.example.marketsync.ui.profile

import androidx.lifecycle.ViewModel
import com.example.marketsync.data.SessionManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val sessionManager: SessionManager
) : ViewModel() {

    fun logout() {
        auth.signOut()
        sessionManager.clearSession()
    }
} 