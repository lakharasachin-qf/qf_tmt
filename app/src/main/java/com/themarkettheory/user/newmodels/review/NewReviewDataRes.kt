package com.themarkettheory.user.newmodels.review


import com.google.gson.annotations.SerializedName

data class NewReviewDataRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: List<ReviewData>? = listOf()
)