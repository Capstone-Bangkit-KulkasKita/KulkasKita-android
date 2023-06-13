package com.example.kukaskitaappv2.ui.addItem

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.kukaskitaappv2.helper.Injection
import com.example.kukaskitaappv2.repository.Repository
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import java.util.*

class AddItemViewModel(
    private val repository: Repository,
    private val userPreferences: UserPreferences
): ViewModel() {
    fun checkToken(): LiveData<String> {
        return userPreferences.getToken().asLiveData()
    }

//    fun addItem(token: String, name:String, expDate: String) =
//        repository.addItem(token, name, expDate)

    class AddItemViewModelFactory private constructor(
        private val repository: Repository,
        private val userPreferences: UserPreferences
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddItemViewModel::class.java)) {
                return AddItemViewModel(repository, userPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: AddItemViewModelFactory? = null

            fun getInstance(
                context: Context,
                userPreferences: UserPreferences
            ): AddItemViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: AddItemViewModelFactory(
                        Injection.provideRepository(context),
                        userPreferences
                    )
                }.also { instance = it }
        }
    }
}