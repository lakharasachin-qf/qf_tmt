package com.themarkettheory.user.newmodels.mypoints


import com.google.gson.annotations.SerializedName

data class ServiceDetails(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("title")
    var title: String? = ""
)