package com.themarkettheory.user.newmodels.getrecentpopularlocation


import com.google.gson.annotations.SerializedName

data class RecentLocation(
    @SerializedName("address")
    var address: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("latitude")
    var latitude: String?,
    @SerializedName("longitude")
    var longitude: String?,
    @SerializedName("place_id")
    var placeId: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("updated_at")
    var updatedAt: String?
)