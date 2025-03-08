package com.example.marketsync.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _signupResult = MutableLiveData<SignupResult>()
    val signupResult: LiveData<SignupResult> = _signupResult

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _signupResult.value = SignupResult.Loading
                Log.d(TAG, "Attempting to create user with email: $email")

                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                
                authResult.user?.let { user ->
                    Log.d(TAG, "User created successfully with uid: ${user.uid}")
                    _signupResult.value = SignupResult.Success
                } ?: run {
                    Log.e(TAG, "Failed to create user: User object is null")
                    _signupResult.value = SignupResult.Error("Failed to create user account")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Signup failed with exception", e)
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> 
                        "Password should be at least 6 characters"
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> 
                        "Invalid email format"
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> 
                        "An account already exists with this email"
                    else -> e.message ?: "Signup failed"
                }
                _signupResult.value = SignupResult.Error(errorMessage)
            }
        }
    }

    companion object {
        private const val TAG = "SignupViewModel"
    }
}

sealed class SignupResult {
    object Loading : SignupResult()
    object Success : SignupResult()
    data class Error(val message: String) : SignupResult()
} 