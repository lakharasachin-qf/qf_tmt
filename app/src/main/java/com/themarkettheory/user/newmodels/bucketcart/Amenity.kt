package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class Amenity(
    @SerializedName("name")
    var name: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?
)