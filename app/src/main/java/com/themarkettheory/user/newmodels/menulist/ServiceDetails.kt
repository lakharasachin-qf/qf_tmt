package com.themarkettheory.user.newmodels.menulist


import com.google.gson.annotations.SerializedName

data class ServiceDetails(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("title")
    var title: String?
)