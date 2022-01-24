package com.themarkettheory.user.newmodels.bucketcart.confirm_order


import com.google.gson.annotations.SerializedName

data class MenuDetail(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("order_id")
    var orderId: Int?,
    @SerializedName("menu_id")
    var menuId: Int?,
    @SerializedName("qty")
    var qty: Int?,
    @SerializedName("price")
    var price: String?,
    @SerializedName("points")
    var points: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("is_redeem")
    var isRedeem: Int?,
    @SerializedName("size_id")
    var sizeId: Int?,
    @SerializedName("menu")
    var menu: Menu?
)