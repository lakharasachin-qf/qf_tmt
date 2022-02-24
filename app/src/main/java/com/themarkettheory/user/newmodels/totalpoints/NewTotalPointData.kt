package com.themarkettheory.user.newmodels.totalpoints


import com.google.gson.annotations.SerializedName

data class NewTotalPointData(
    @SerializedName("points")
    var points: Int? = 0,
    @SerializedName("service_id")
    var serviceId: Int? = 0,
    @SerializedName("service_details")
    var serviceDetails: ServiceDetails? = ServiceDetails(),
    @SerializedName("total")
    var total: String? = "",
    var colorCode :String?
)