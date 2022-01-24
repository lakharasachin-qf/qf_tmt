package com.themarkettheory.user.newmodels.totalpoints


import com.google.gson.annotations.SerializedName

data class ServiceDetails(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("category_id")
    var categoryId: Int? = 0,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("latitude")
    var latitude: String? = "",
    @SerializedName("longitude")
    var longitude: String? = "",
    @SerializedName("rating")
    var rating: String? = "",
    @SerializedName("total_rating")
    var totalRating: String? = "",
    @SerializedName("is_veg")
    var isVeg: Int? = 0,
    @SerializedName("is_non_veg")
    var isNonVeg: Int? = 0,
    @SerializedName("is_egg")
    var isEgg: Int? = 0,
    @SerializedName("currency")
    var currency: String? = ""
)