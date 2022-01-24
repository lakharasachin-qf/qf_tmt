package com.themarkettheory.user.model


import com.google.gson.annotations.SerializedName

data class GetRecentPopularLocationResponse(
    @SerializedName("data")
    val `data`: RecentPopularLocationData?,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)