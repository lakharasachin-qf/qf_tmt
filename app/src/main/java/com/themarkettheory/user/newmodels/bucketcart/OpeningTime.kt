package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class OpeningTime(
    @SerializedName("day")
    var day: String?,
    @SerializedName("hours")
    var hours: List<Hour>?
)