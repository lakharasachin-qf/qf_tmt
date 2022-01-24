package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class RetailMenuDetailResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: RetailDetailData? = null
}

class Size {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("menu_id")
    @Expose
    var menuId: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("is_available")
    @Expose
    var isAvailable: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
}

class RetailDetailData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("ingredients")
    @Expose
    var ingredients: String? = null

    @SerializedName("unit")
    @Expose
    var unit: String? = null

    @SerializedName("cuisine_id")
    @Expose
    var cuisineId: Int? = null

    @SerializedName("price")
    @Expose
    var price: Int? = null

    @SerializedName("discounted_price")
    @Expose
    var discountedPrice: String? = null

    @SerializedName("final_price")
    @Expose
    var finalPrice: String? = null

    @SerializedName("point")
    @Expose
    var point: Int? = null

    @SerializedName("in_cart")
    @Expose
    var in_cart: Int? = null

    @SerializedName("cart_qty")
    @Expose
    var cart_qty: Int? = null

    @SerializedName("is_nonveg")
    @Expose
    var isNonveg: Int? = null

    @SerializedName("is_spicy")
    @Expose
    var isSpicy: Int? = null

    @SerializedName("is_recommonded")
    @Expose
    var isRecommonded: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("is_available")
    @Expose
    var isAvailable: Int? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null

    @SerializedName("is_hot")
    @Expose
    var isHot: Int? = null

    @SerializedName("only_in_restro")
    @Expose
    var onlyInRestro: Int? = null

    @SerializedName("extra_topping")
    @Expose
    var extraTopping: Int? = null

    @SerializedName("user_customizable")
    @Expose
    var userCustomizable: Int? = null

    @SerializedName("size_data")
    @Expose
    var sizeData: String? = null

    @SerializedName("custom_data")
    @Expose
    var customData: String? = null

    @SerializedName("sizes")
    @Expose
    var sizes: List<Size>? = null
}