package com.themarkettheory.user.newmodels.orderconfirmation


import com.google.gson.annotations.SerializedName

data class ServiceDetails(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("category_id")
    var categoryId: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("address")
    var address: String?,
    @SerializedName("latitude")
    var latitude: String?,
    @SerializedName("longitude")
    var longitude: String?,
    @SerializedName("rating")
    var rating: String?,
    @SerializedName("total_rating")
    var totalRating: String?,
    @SerializedName("is_veg")
    var isVeg: Int?,
    @SerializedName("is_non_veg")
    var isNonVeg: Int?,
    @SerializedName("is_egg")
    var isEgg: Int?,
    @SerializedName("currency")
    var currency: String?
)