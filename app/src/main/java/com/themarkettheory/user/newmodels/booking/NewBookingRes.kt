package com.themarkettheory.user.newmodels.booking


import com.google.gson.annotations.SerializedName

data class NewBookingRes(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
)