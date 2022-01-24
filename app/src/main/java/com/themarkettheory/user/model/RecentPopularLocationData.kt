package com.themarkettheory.user.model


import com.google.gson.annotations.SerializedName

data class RecentPopularLocationData(
    @SerializedName("popular_location")
    val popularLocation: List<PopularLocation>,
    @SerializedName("recent_location")
    val recentLocation: List<RecentLocation>
)