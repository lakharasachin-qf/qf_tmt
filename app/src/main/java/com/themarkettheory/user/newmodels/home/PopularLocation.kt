package com.themarkettheory.user.newmodels.home


import com.google.gson.annotations.SerializedName

data class PopularLocation(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("latitude")
    var latitude: String? = "",
    @SerializedName("longitude")
    var longitude: String? = "",
    @SerializedName("place_id")
    var placeId: String? = "",
    @SerializedName("type")
    var type: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = ""
)