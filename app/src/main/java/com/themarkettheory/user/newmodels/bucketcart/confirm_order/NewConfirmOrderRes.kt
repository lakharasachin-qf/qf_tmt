package com.themarkettheory.user.newmodels.bucketcart.confirm_order


import com.google.gson.annotations.SerializedName

data class NewConfirmOrderRes(
    @SerializedName("status")
    var status: Int?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("order_id")
    var orderId: Long?,
    @SerializedName("data")
    var `data`: Data?
)