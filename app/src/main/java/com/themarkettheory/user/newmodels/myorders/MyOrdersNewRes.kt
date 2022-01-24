package com.themarkettheory.user.newmodels.myorders


import com.google.gson.annotations.SerializedName

data class MyOrdersNewRes(
    @SerializedName("status")
    var status: Int?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("data")
    var `data`: List<MyOrderNewData>?
)