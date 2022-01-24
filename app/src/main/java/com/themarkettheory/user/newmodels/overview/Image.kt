package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("service_id")
    var serviceId: Int? = 0,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("type")
    var type: Int? = 0,
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("video_url")
    var videoUrl: String? = ""
)