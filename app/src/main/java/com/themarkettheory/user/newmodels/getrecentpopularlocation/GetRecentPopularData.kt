package com.themarkettheory.user.newmodels.getrecentpopularlocation


import com.google.gson.annotations.SerializedName

data class GetRecentPopularData(
    @SerializedName("popular_location")
    var popularLocation: List<PopularLocation>?,
    @SerializedName("recent_location")
    var recentLocation: List<RecentLocation>?
)