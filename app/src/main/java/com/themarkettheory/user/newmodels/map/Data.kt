package com.themarkettheory.user.newmodels.map


import com.google.gson.annotations.SerializedName
import com.themarkettheory.user.newmodels.home.Offer
import com.themarkettheory.user.newmodels.home.PopularLocation

data class Data(
    @SerializedName("restaurant")
    var restaurant: List<Restaurant>? = listOf(),
    @SerializedName("offers")
    var offers: List<Offer>? = listOf(),
    @SerializedName("popular_locations")
    var popularLocations: List<PopularLocation>? = listOf(),
    @SerializedName("page")
    var page: Int? = 0
)