package com.themarkettheory.user.newmodels.updateprofile


import com.google.gson.annotations.SerializedName

data class NewUpdateProfileRes(
    @SerializedName("status")
    var status: Int?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("data")
    var `data`: Data?
)