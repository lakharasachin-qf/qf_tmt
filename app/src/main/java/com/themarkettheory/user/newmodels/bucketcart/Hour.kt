package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class Hour(
    @SerializedName("time")
    var time: String?
)