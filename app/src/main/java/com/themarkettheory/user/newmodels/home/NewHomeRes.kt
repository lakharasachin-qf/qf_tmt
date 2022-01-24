package com.themarkettheory.user.newmodels.home


import com.google.gson.annotations.SerializedName

data class NewHomeRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: Data? = Data()
)