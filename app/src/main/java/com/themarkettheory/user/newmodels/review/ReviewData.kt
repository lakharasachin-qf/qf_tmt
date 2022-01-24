package com.themarkettheory.user.newmodels.review


import com.google.gson.annotations.SerializedName

data class ReviewData(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("user_id")
    var userId: Int? = 0,
    @SerializedName("service_id")
    var serviceId: Int? = 0,
    @SerializedName("rating")
    var rating: String? = "",
    @SerializedName("customer_service")
    var customerService: String? = "",
    @SerializedName("restaurant_cleanliness")
    var restaurantCleanliness: String? = "",
    @SerializedName("food_quality")
    var foodQuality: String? = "",
    @SerializedName("recommend_friends")
    var recommendFriends: String? = "",
    @SerializedName("comment")
    var comment: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("order_id")
    var orderId: Int? = 0,
    @SerializedName("user")
    var user: User? = User(),
    @SerializedName("review_time")
    var reviewTime: String? = ""
)