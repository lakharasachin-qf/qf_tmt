package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetMenuResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: MenuData? = null
}

class MenuData {
    @SerializedName("list")
    @Expose
    var list: List<MenuList>? = null

    @SerializedName("recommanded")
    @Expose
    var recommanded: List<Menu>? = null
}

class MenuList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("menu")
    @Expose
    var menu: ArrayList<Menu>? = null

    @SerializedName("menu_base_url")
    @Expose
    var menuBaseUrl: String? = null
}

class Menu {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("preparing_time")
    @Expose
    var preparing_time: String? = null

    @SerializedName("cuisines_name")
    @Expose
    var cuisines_name: String? = null

    @SerializedName("cuisine_id")
    @Expose
    var cuisineId: Int? = null

    @SerializedName("only_in_restro")
    @Expose
    var only_in_restro: Int? = null

    @SerializedName("live_deals")
    @Expose
    var live_deals: LiveDeals? = null

    /*@SerializedName("in_cart")
    @Expose
    var in_cart: Int? = null

    @SerializedName("cart_qty")
    @Expose
    var cart_qty: Int? = null*/

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("discounted_price")
    @Expose
    var discountedPrice: String? = null

    @SerializedName("point")
    @Expose
    var point: Int? = null

    @SerializedName("redeem_points")
    @Expose
    var redeem_points: Int? = null

    @SerializedName("is_nonveg")
    @Expose
    var isNonveg: Int? = null

    @SerializedName("is_spicy")
    @Expose
    var isSpicy: Int? = null

    @SerializedName("is_recommonded")
    @Expose
    var isRecommonded: Int? = null

    @SerializedName("size_data")
    @Expose
    var size_data: List<SizeDatum>? = null

    @SerializedName("custom_data")
    @Expose
    var custom_data: List<SizeDatum>? = null

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

    @SerializedName("category_id")
    @Expose
    var category_id: Int? = null

    @SerializedName("in_cart")
    @Expose
    var in_cart: Int? = null

    @SerializedName("cart_qty")
    @Expose
    var cart_qty: Int? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("food_qty")
    @Expose
    var food_qty: String? = null

    @SerializedName("unit")
    @Expose
    var unit: String? = null

    @SerializedName("activated_coupon")
    @Expose
    var activated_coupon: List<OfferDatum>? = null

    var isSelected: Boolean? = false
    var finalAmount: Float = 0.0f
    var payableAmount: Float = 0.0f
}

data class LiveDeals(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("service_id")
    val serviceID: Long? = null,

    @SerializedName("menu_id")
    val menuID: Long? = null,

    @SerializedName("day")
    val day: String? = null,

    @SerializedName("start_time")
    val startTime: String? = null,

    @SerializedName("end_time")
    val endTime: String? = null,

    @SerializedName("is_auto")
    val isAuto: Long? = null,

    @SerializedName("status")
    val status: Long? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
) : Serializable

class SizeDatum {
    @SerializedName("regular")
    @Expose
    var regular: Boolean? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null
}

class CustomDatum {
    @SerializedName("regular")
    @Expose
    var regular: Boolean? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null
}


