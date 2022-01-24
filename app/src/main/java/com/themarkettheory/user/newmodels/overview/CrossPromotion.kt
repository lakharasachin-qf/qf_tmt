package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class CrossPromotion(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("service_id")
    var serviceId: Int? = 0,
    @SerializedName("promotion_service_id")
    var promotionServiceId: Int? = 0,
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("category_id")
    var categoryId: Int? = 0
)