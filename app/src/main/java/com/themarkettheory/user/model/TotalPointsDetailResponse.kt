package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TotalPointsDetailResponse {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: TotalPointsDetailData? = null
}

class TotalPointsDetailData {
    @SerializedName("list")
    @Expose
    var list: ArrayList<TotalPointDetailList>? = null

    @SerializedName("restaurant_base_url")
    @Expose
    var restaurantBaseUrl: String? = null

    @SerializedName("total_points")
    @Expose
    var totalPoints: Int? = null

    @SerializedName("service_details")
    @Expose
    var serviceDetails: ServiceDetail? = null
}

class TotalPointDetailList {
    @SerializedName("menu_title")
    @Expose
    var menuTitle: String? = null

    @SerializedName("cuisines_name")
    @Expose
    var cuisinesName: String? = null

    @SerializedName("is_nonveg")
    @Expose
    var isNonveg: Int? = null

    @SerializedName("is_spicy")
    @Expose
    var isSpicy: Int? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("menu_id")
    @Expose
    var menuId: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("price")
    @Expose
    var price: Int? = null

    @SerializedName("discounted_price")
    @Expose
    var discountedPrice: String? = null

    @SerializedName("redeem_points")
    @Expose
    var redeemPoints: Int? = null

    @SerializedName("point")
    @Expose
    var point: Int? = null

    @SerializedName("in_cart")
    @Expose
    var in_cart: Int? = null

    @SerializedName("cart_qty")
    @Expose
    var cart_qty: Int? = null

    @SerializedName("is_redeem")
    @Expose
    var is_redeem: Int? = null
    var adapterPosition: Int = 0
}
