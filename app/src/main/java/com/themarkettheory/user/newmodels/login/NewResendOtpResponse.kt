package com.themarkettheory.user.newmodels.login


import com.google.gson.annotations.SerializedName

data class NewResendOtpResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)