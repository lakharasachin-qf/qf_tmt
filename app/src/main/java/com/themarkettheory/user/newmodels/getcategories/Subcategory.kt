package com.themarkettheory.user.newmodels.getcategories


import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Subcategory : Serializable {
    @SerializedName("category_id")
    var categoryId: Int? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("icon")
    var icon: String? = null

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}