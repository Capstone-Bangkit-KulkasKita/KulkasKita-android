package com.example.kukaskitaappv2.source.remote.response

import com.google.gson.annotations.SerializedName

data class FoodResponse(

	@field:SerializedName("food")
	val food: List<FoodResponseItem>
)

data class FoodResponseItem(

	@field:SerializedName("idUser")
	val idUser: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("food_id")
	val id: Int,

	@field:SerializedName("expDate")
	val expDate: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
