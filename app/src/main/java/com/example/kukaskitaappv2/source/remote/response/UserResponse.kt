package com.example.kukaskitaappv2.source.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("loginResult")
	val loginResult: LoginResult? = null,

	@field:SerializedName("error")
	val error: Boolean? = null
)

data class LoginResult(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("food")
	val food: List<FoodItem?>? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class FoodItem(

	@field:SerializedName("idUser")
	val idUser: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("expDate")
	val expDate: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
