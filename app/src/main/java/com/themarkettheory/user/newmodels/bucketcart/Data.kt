package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("list")
    var list: List<CartList>?,
    @SerializedName("booking")
    var booking: Booking?,
    @SerializedName("service_details")
    var serviceDetails: ServiceDetails?
)