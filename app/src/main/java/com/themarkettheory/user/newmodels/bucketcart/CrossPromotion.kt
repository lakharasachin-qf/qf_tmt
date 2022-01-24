package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class CrossPromotion(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("service_id")
    var serviceId: Int?,
    @SerializedName("promotion_service_id")
    var promotionServiceId: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("category_id")
    var categoryId: Int?
)