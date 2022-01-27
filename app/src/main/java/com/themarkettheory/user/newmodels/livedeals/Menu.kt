package com.themarkettheory.user.newmodels.livedeals


import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("ingredients")
    var ingredients: String? = "",
    @SerializedName("unit")
    var unit: String? = "",
    @SerializedName("final_price")
    var finalPrice: Int? = 0,
    @SerializedName("point")
    var point: Int? = 0,
    @SerializedName("is_spicy")
    var isSpicy: Int? = 0,
    @SerializedName("is_recommonded")
    var isRecommonded: Int? = 0,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("tax")
    var tax: Int? = 0,
    @SerializedName("extra_topping")
    var extraTopping: Int? = 0,
    @SerializedName("user_customizable")
    var userCustomizable: Int? = 0,
    @SerializedName("size_data")
    var sizeData: String? = "",
    @SerializedName("custom_data")
    var customData: String? = "",
    @SerializedName("currency")
    var currency: String? = "",
    @SerializedName("redeem_points")
    var redeemPoints: Int? = 0,
    @SerializedName("is_deleted")
    var isDeleted: Int? = 0,
    @SerializedName("preparing_time")
    var preparingTime: String? = "",
    @SerializedName("booking_id")
    var bookingId: Int? = 0,
    @SerializedName("in_cart")
    var inCart: Int? = 0,
    @SerializedName("cart_qty")
    var cartQty: Int? = 0,
    @SerializedName("is_redeem")
    var isRedeem: Int? = 0,
    @SerializedName("redeem_qty")
    var redeemQty: Int? = 0,
    @SerializedName("dish_qty")
    var dishQty: String? = "",
    @SerializedName("food_type")
    var foodType: Int? = 0,
    @SerializedName("category_id")
    var categoryId: Int? = 0,
    @SerializedName("is_enable")
    var isEnable: Int? = 0,
    @SerializedName("category_name")
    var categoryName: String? = "",
    @SerializedName("actual_price")
    var actualPrice: Int? = 0
)