package com.themarkettheory.user.newmodels.getcategories


import com.google.gson.annotations.SerializedName

data class NewGetCategoriesRes(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
)