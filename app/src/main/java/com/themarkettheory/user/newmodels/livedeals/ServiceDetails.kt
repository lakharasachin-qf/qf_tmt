package com.themarkettheory.user.newmodels.livedeals


import com.google.gson.annotations.SerializedName

data class ServiceDetails(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("title")
    var title: String? = ""
)