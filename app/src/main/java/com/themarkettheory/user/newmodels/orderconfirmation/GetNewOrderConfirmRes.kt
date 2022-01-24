package com.themarkettheory.user.newmodels.orderconfirmation


import com.google.gson.annotations.SerializedName

data class GetNewOrderConfirmRes(
    @SerializedName("status")
    var status: Int?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("data")
    var `data`: Data?
)