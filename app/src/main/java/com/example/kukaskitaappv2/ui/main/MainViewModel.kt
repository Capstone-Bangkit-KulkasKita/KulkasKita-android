package com.example.kukaskitaappv2.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kukaskitaappv2.helper.Injection
import com.example.kukaskitaappv2.repository.Repository
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.source.remote.response.FoodResponseItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
): ViewModel() {

    val list: LiveData<List<FoodResponseItem>> = repository.list
    fun getListFood(myToken:String) = repository.getFood(myToken)
    fun checkToken(): LiveData<String> {
        return repository.getToken()
    }
    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logout()
        }
    }

    class MainViewModelFactory private constructor(
        private val repository: Repository,
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: MainViewModelFactory? = null
            fun getInstance(context: Context, userPreferences: UserPreferences): MainViewModelFactory = instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideRepository(context),
                )
            }.also { instance = it }
        }
    }
}