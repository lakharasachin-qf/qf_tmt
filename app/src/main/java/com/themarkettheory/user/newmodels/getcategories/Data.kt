package com.themarkettheory.user.newmodels.getcategories


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("categories")
    var categories: List<Category>?
)