package com.themarkettheory.user.newmodels.home


import com.google.gson.annotations.SerializedName

data class Slider(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("sort")
    var sort: Int? = 0,
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = ""
)