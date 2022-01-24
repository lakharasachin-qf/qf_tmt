package com.themarkettheory.user.newmodels.livedeals


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("service_details")
    var serviceDetails: ServiceDetails? = ServiceDetails(),
    @SerializedName("list")
    var list: List<LiveDealList>? = listOf()
)