package com.example.kukaskitaappv2.ui.register

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kukaskitaappv2.helper.Injection
import com.example.kukaskitaappv2.repository.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {
    fun register(email: String, password: String) = repository.registerUser(email, password)

    class RegisterViewModelFactory private constructor(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                return RegisterViewModel(repository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: RegisterViewModelFactory? = null

            fun getInstance(context: Context, dataStore: DataStore<Preferences>): RegisterViewModelFactory = instance ?: synchronized(this) {
                instance ?: RegisterViewModelFactory(Injection.provideRepository(context))
            }
        }
    }
}