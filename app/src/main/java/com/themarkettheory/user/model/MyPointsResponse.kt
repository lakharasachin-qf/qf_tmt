package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyPointsResponse {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: MyPointsData? = null
}

class MyPointsData {
    @SerializedName("list")
    @Expose
    var list: ArrayList<MyPointsList>? = null

    @SerializedName("menu_base_url")
    @Expose
    var menuBaseUrl: String? = null

    @SerializedName("total_points")
    @Expose
    var totalPoints: Int? = null
}

class MyPointsList {
    @SerializedName("menu_title")
    @Expose
    var menuTitle: String? = null

    @SerializedName("cuisines_name")
    @Expose
    var cuisinesName: String? = null

    @SerializedName("point")
    @Expose
    var points: Int? = null

    @SerializedName("is_nonveg")
    @Expose
    var isNonveg: Int? = null

    @SerializedName("is_spicy")
    @Expose
    var isSpicy: Int? = null

    @SerializedName("mypoints_id")
    @Expose
    var mypointsId: Int? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("menu_id")
    @Expose
    var menu_id: Int? = null

    @SerializedName("service_id")
    @Expose
    var service_id: Int? = null

    @SerializedName("price")
    @Expose
    var price: Int? = null

    @SerializedName("redeem_points")
    @Expose
    var redeem_points: Int? = null

    @SerializedName("is_redeem")
    @Expose
    var is_redeem: Int? = null

    @SerializedName("in_cart")
    @Expose
    var in_cart: Int? = null

    @SerializedName("cart_qty")
    @Expose
    var cart_qty: Int? = null

    @SerializedName("discounted_price")
    @Expose
    var discounted_price: String? = null

    var adapterPosition: Int = 0
}
