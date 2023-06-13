package com.example.kukaskitaappv2.source.remote.retrofit

import com.example.kukaskitaappv2.source.remote.response.RegisterResponse
import com.example.kukaskitaappv2.source.remote.response.UserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("users/register")
    fun register(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>


}