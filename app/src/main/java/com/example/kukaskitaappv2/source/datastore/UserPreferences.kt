package com.example.kukaskitaappv2.source.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {
    private val token = stringPreferencesKey("myToken")

    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[token] ?: "null"
        }
    }


    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[this.token] = token
        }
    }

    suspend fun deleteToken() {
        dataStore.edit {
            it[token] = "null"
        }
    }



    companion object {
        @Volatile
        private var instance: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences =
            instance ?: synchronized(this) {
                instance ?: UserPreferences(dataStore)
            }.also { instance = it }
    }
}