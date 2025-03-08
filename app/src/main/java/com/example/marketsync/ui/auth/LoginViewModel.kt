package com.example.marketsync.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketsync.data.SessionManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun loginWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginResult.value = LoginResult.Loading
                Log.d(TAG, "Attempting login with email: $email")
                
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    Log.d(TAG, "Login successful for user: ${user.uid}")
                    sessionManager.saveUserId(user.uid)
                    user.getIdToken(false).await()?.token?.let { token ->
                        sessionManager.saveAuthToken(token)
                        Log.d(TAG, "Auth token saved successfully")
                    }
                    _loginResult.value = LoginResult.Success
                } ?: run {
                    Log.e(TAG, "Login failed: User object is null")
                    _loginResult.value = LoginResult.Error("Login failed: Unable to retrieve user information")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login failed with exception", e)
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "No account exists with this email"
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                    else -> e.message ?: "Login failed"
                }
                _loginResult.value = LoginResult.Error(errorMessage)
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}

sealed class LoginResult {
    object Loading : LoginResult()
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
} 