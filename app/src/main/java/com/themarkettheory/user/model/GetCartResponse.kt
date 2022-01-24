package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCartResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: CartData? = null
}

class CartList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("menu_id")
    @Expose
    var menuId: Int? = null

    @SerializedName("qty")
    @Expose
    var qty: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null

    @SerializedName("category_id")
    @Expose
    var category_id: Any? = null

    @SerializedName("is_redeem")
    @Expose
    var is_redeem: String? = null

    @SerializedName("menu")
    @Expose
    var menu: Menu? = null

}

class Tax {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("tax")
    @Expose
    var tax: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
}

class CartServiceDetail {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("category_id")
    @Expose
    var category_id: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("currency_str")
    @Expose
    var currency_str: String? = null
}

class CartData {
    @SerializedName("list")
    @Expose
    var list: ArrayList<CartList>? = null

    @SerializedName("tax")
    @Expose
    var tax: List<Tax>? = null

    @SerializedName("service_details")
    @Expose
    var service_details: CartServiceDetail? = null

    @SerializedName("activated_coupon")
    @Expose
    var activated_coupon: List<OfferDatum>? = null
}