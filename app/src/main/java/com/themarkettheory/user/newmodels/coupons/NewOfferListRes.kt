package com.themarkettheory.user.newmodels.coupons


import com.google.gson.annotations.SerializedName

data class NewOfferListRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: List<NewOfferListData>? = listOf()
)