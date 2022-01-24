package com.themarkettheory.user.newmodels.menulist


import com.google.gson.annotations.SerializedName

data class NewMenuListRes(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
)