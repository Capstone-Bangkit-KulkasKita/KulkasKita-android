package com.example.kukaskitaappv2.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.kukaskitaappv2.repository.Repository
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.source.remote.retrofit.ApiConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myToken")
object Injection {
    fun provideRepository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences.getInstance(context.dataStore)
        return Repository.getInstance(apiService,userPreferences)
    }

}