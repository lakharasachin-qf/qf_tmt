package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class Occasion(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = ""

) {
    override fun toString(): String {
        return name!!
    }
}