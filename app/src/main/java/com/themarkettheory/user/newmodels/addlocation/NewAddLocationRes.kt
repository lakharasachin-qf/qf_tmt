package com.themarkettheory.user.newmodels.addlocation


import com.google.gson.annotations.SerializedName

data class NewAddLocationRes(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
)