package com.themarkettheory.user.newmodels.livedeals


import com.google.gson.annotations.SerializedName

data class NewLiveDealRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: Data? = Data()
)