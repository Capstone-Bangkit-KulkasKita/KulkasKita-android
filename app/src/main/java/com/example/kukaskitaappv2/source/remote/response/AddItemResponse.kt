package com.example.kukaskitaappv2.source.remote.response

import com.google.gson.annotations.SerializedName

data class AddItemResponse(

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
