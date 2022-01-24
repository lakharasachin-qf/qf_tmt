package com.themarkettheory.user.newmodels.changepassword


import com.google.gson.annotations.SerializedName

data class ChangePasswordRes(
    @SerializedName("status")
    var status: Int?,
    @SerializedName("message")
    var message: String?
)