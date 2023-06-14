package com.example.kukaskitaappv2.source.remote.retrofit

import com.example.kukaskitaappv2.source.remote.response.AddItemResponse
import com.example.kukaskitaappv2.source.remote.response.FoodResponse
import com.example.kukaskitaappv2.source.remote.response.RegisterResponse
import com.example.kukaskitaappv2.source.remote.response.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

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

    @FormUrlEncoded
    @POST("food/create")
    fun addItem(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("expDate") expDate: String
    ): Call<AddItemResponse>

    @GET("food/")
    suspend fun getFood(
        @Header("Authorization") token: String,
    ):Response<FoodResponse>

}