package com.themarkettheory.user.newmodels.menulist


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("menu")
    var menu: List<Menu>?,
    @SerializedName("service_details")
    var serviceDetails: ServiceDetails?
)