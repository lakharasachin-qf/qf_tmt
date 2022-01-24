package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class NewServiceDetailsRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: Data? = Data()
)