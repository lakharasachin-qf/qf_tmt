package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("type_id")
    var typeId: Int? = 0,
    @SerializedName("type")
    var type: String? = ""
)