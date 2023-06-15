package com.example.kukaskitaappv2.source.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("refresh_token")
	val refreshToken: Any? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
