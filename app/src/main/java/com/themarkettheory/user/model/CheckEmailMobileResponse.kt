package com.themarkettheory.user.model


import com.google.gson.annotations.SerializedName

data class CheckEmailMobileResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("otp")
    val otp: Int
)