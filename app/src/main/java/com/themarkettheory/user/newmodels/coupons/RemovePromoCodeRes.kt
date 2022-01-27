package com.themarkettheory.user.newmodels.coupons


import com.google.gson.annotations.SerializedName

data class RemovePromoCodeRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("is_feedback")
    var isValid: String? = ""
)