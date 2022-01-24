package com.themarkettheory.user.newmodels.home


import com.google.gson.annotations.SerializedName

data class Subcategory(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("icon")
    var icon: String? = "",
    @SerializedName("category_id")
    var categoryId: Int? = 0,
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = ""
)