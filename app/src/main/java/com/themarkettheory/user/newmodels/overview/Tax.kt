package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class Tax(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("tax")
    var tax: String? = "",
    @SerializedName("created_at")
    var createdAt: Any? = Any(),
    @SerializedName("updated_at")
    var updatedAt: Any? = Any()
)