package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class OpeningTime(
    @SerializedName("day")
    var day: String? = "",
    @SerializedName("hours")
    var hours: List<Hour>? = listOf()
)