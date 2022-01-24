package com.themarkettheory.user.newmodels.totalpoints


import com.google.gson.annotations.SerializedName

data class NewTotalPointRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: List<NewTotalPointData>? = listOf(),
    @SerializedName("total_points")
    var totalPoints: Int? = 0
)