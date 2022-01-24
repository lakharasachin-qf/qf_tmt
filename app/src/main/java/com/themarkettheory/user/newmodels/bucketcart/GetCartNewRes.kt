package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class GetCartNewRes(
    @SerializedName("status")
    var status: Int?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("data")
    var `data`: Data?
)