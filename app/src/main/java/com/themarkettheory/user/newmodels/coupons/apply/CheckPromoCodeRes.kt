package com.themarkettheory.user.newmodels.coupons.apply


import com.google.gson.annotations.SerializedName

data class CheckPromoCodeRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("is_valid")
    var isValid: Int? = 0
)