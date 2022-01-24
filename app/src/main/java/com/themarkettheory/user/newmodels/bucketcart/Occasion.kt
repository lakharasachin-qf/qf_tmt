package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class Occasion(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?
)