package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("service_id")
    var serviceId: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("type")
    var type: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("video_url")
    var videoUrl: String?
)