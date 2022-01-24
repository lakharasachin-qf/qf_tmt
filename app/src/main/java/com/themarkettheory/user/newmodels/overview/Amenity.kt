package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class Amenity(
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = ""
)