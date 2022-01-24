package com.themarkettheory.user.newmodels.addreview


import com.google.gson.annotations.SerializedName

data class AddReviewRes(
    @SerializedName("status")
    var status: Int?,
    @SerializedName("message")
    var message: String?
)