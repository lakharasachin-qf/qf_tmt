package com.themarkettheory.user.newmodels.menulist


import com.google.gson.annotations.SerializedName

data class MenuX(
    @SerializedName("actual_price")
    var actualPrice: Double?,
    @SerializedName("cart_qty")
    var cartQty: Int?,
    @SerializedName("category_id")
    var categoryId: Int?,
    @SerializedName("category_name")
    var categoryName: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("currency")
    var currency: String?,
    @SerializedName("custom_data")
    var customData: String?,
    @SerializedName("dish_qty")
    var dishQty: String?,
    @SerializedName("extra_topping")
    var extraTopping: Int?,
    @SerializedName("final_price")
    var finalPrice: Double?,
    @SerializedName("food_type")
    var foodType: Int?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("in_cart")
    var inCart: Int?,
    @SerializedName("ingredients")
    var ingredients: String?,
    @SerializedName("is_deleted")
    var isDeleted: Int?,
    @SerializedName("is_enable")
    var isEnable: Int?,
    @SerializedName("is_recommonded")
    var isRecommonded: Int?,
    @SerializedName("is_spicy")
    var isSpicy: Int?,
    @SerializedName("point")
    var point: Int?,
    @SerializedName("preparing_time")
    var preparingTime: String?,
    @SerializedName("redeem_points")
    var redeemPoints: Int?,
    @SerializedName("size_data")
    var sizeData: String?,
    @SerializedName("tax")
    var tax: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("unit")
    var unit: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("user_customizable")
    var userCustomizable: Int?
)