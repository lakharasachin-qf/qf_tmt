package com.themarkettheory.user.newmodels.home


import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("icon")
    var icon: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("subcategory")
    var subcategory: List<Subcategory>? = listOf()
)