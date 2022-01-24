package com.themarkettheory.user.newmodels.map


import com.google.gson.annotations.SerializedName

data class NewServiceListRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: Data? = Data()
)