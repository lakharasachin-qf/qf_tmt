package com.themarkettheory.user.newmodels.getrecentpopularlocation


import com.google.gson.annotations.SerializedName

data class NewGetRecentPopularLocation(
    @SerializedName("data")
    var getRecentPopularData: GetRecentPopularData?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
)