package com.themarkettheory.user.newmodels.searchrestaurant


import com.google.gson.annotations.SerializedName

data class SearchRestaurantRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: List<SearchRestaurantData>? = listOf()
)