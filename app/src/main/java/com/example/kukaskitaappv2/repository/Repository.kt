package com.example.kukaskitaappv2.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.kukaskitaappv2.helper.ResultState
import com.example.kukaskitaappv2.paging.FoodPagingSource
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.source.remote.response.*
import com.example.kukaskitaappv2.source.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.Date

class Repository private constructor(
    private val apiService: ApiService,
    private val preferences: UserPreferences
) {
    private val registerResult = MediatorLiveData<ResultState<RegisterResponse>>()
    private val loginResult = MediatorLiveData<ResultState<UserResponse>>()
    private val addItemResult = MediatorLiveData<ResultState<AddItemResponse>>()
    private val _list = MutableLiveData<FoodResponse>()
    val list: LiveData<FoodResponse> = _list

    fun getFood(): LiveData<PagingData<FoodResponseItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                FoodPagingSource(preferences, apiService)
            }
        ).liveData
    }

    fun addItem(token: String, name:String, expDate: String): LiveData<ResultState<AddItemResponse>> {
        addItemResult.value = ResultState.Loading

        val client = apiService.addItem(token, name, expDate)
        client.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(
                call: Call<AddItemResponse>,
                response: Response<AddItemResponse>
            ) {
                if (response.isSuccessful) {
                    val responseInfo = response.body()
                    if (responseInfo != null) {
                        addItemResult.postValue(ResultState.Success(responseInfo))
                    } else {
                        addItemResult.postValue(ResultState.Error(POST_ERROR))
                        Log.e(TAG, "Failed: add Item is null")
                    }
                } else {
                    addItemResult.postValue(ResultState.Error(POST_ERROR))
                    Log.e(TAG, "Failed: add Item response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                addItemResult.postValue(ResultState.Error(POST_ERROR))
                Log.e(TAG, "Failed: story post response failure - ${t.message.toString()}")
            }
        })

        return addItemResult
    }

    fun registerUser(email: String, password: String): LiveData<ResultState<RegisterResponse>> {
        registerResult.value = ResultState.Loading
        val client = apiService.register(email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerStatus = response.body()
                    if (registerStatus != null) {
                        registerResult.value = ResultState.Success(registerStatus)
                    } else {
                        registerResult.value = ResultState.Error(REGISTER_ERROR)
                    }
                } else {
                    registerResult.value = ResultState.Error(REGISTER_ERROR)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = ResultState.Error(REGISTER_ERROR)
            }

        })

        return registerResult
    }

    fun loginUser(email: String, password: String): LiveData<ResultState<UserResponse>> {
        loginResult.value = ResultState.Loading
        val client = apiService.login(
            email,
            password
        )

        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val loginInfo = response.body()
                    if (loginInfo != null) {
                        loginResult.value = ResultState.Success(loginInfo)
                    } else {
                        loginResult.value = ResultState.Error(LOGIN_ERROR)
                    }
                } else {
                    loginResult.value = ResultState.Error(LOGIN_ERROR)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                loginResult.value = ResultState.Error(LOGIN_ERROR)
            }
        })

        return loginResult
    }

    fun getToken(): LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    suspend fun logout() {
        preferences.deleteToken()
    }

    companion object{
        private const val POST_ERROR = "Error!Item was not added."
        private val TAG = Repository::class.java.simpleName
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            apiService: ApiService,
            preferences: UserPreferences
        ):Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService,preferences)
            }.also { instance = it }

        private const val REGISTER_ERROR = "Register Failed!"
        private const val LOGIN_ERROR = "Login Failed!"
    }
}