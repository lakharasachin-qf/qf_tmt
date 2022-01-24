package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class CartList(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("user_id")
    var userId: Int?,
    @SerializedName("menu_id")
    var menuId: Int?,
    @SerializedName("qty")
    var qty: Int?,
    @SerializedName("service_id")
    var serviceId: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("is_redeem")
    var isRedeem: Int?,
    @SerializedName("booking_id")
    var bookingId: Int?,
    @SerializedName("menu")
    var menu: Menu?
)