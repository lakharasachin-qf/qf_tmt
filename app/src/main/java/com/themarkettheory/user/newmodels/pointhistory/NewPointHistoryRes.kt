package com.themarkettheory.user.newmodels.pointhistory


import com.google.gson.annotations.SerializedName

data class NewPointHistoryRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: List<NewPointHistoryData>? = listOf(),
    @SerializedName("total_points")
    var totalPoints: Int? = 0
)