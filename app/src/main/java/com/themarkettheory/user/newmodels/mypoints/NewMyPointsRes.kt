package com.themarkettheory.user.newmodels.mypoints


import com.google.gson.annotations.SerializedName

data class NewMyPointsRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: List<NewMyPointData>? = listOf(),
    @SerializedName("total_points")
    var totalPoints: Int? = 0,
    @SerializedName("service_details")
    var serviceDetails: ServiceDetails? = ServiceDetails()
)