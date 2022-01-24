package com.themarkettheory.user.model

import com.google.gson.annotations.SerializedName

data class AddLocationResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String
)
