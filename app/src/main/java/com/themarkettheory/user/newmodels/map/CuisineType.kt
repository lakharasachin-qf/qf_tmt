package com.themarkettheory.user.newmodels.map


import com.google.gson.annotations.SerializedName

data class CuisineType(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("service_id")
    var serviceId: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = ""
)