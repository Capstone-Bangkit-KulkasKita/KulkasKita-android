package com.example.kukaskitaappv2.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kukaskitaappv2.helper.Injection
import com.example.kukaskitaappv2.repository.Repository
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: Repository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun login(email: String, password: String) = repository.loginUser(email, password)

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.saveToken(token)
        }
    }

    class LoginViewModelFactory private constructor(
        private val repository: Repository,
        private val userPreferences: UserPreferences
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(repository, userPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: LoginViewModelFactory? = null
            fun getInstance(
                context: Context,
                userPreferences: UserPreferences
            ): LoginViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: LoginViewModelFactory(
                        Injection.provideRepository(context),
                        userPreferences
                    )
                }
        }
    }
}