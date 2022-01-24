package com.themarkettheory.user.newmodels.orderconfirmation


import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("ingredients")
    var ingredients: String?,
    @SerializedName("unit")
    var unit: String?,
    @SerializedName("final_price")
    var finalPrice: String?,
    @SerializedName("point")
    var point: Int?,
    @SerializedName("is_spicy")
    var isSpicy: Int?,
    @SerializedName("is_recommonded")
    var isRecommonded: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("tax")
    var tax: Int?,
    @SerializedName("extra_topping")
    var extraTopping: Int?,
    @SerializedName("user_customizable")
    var userCustomizable: Int?,
    @SerializedName("size_data")
    var sizeData: String?,
    @SerializedName("custom_data")
    var customData: String?,
    @SerializedName("currency")
    var currency: String?,
    @SerializedName("redeem_points")
    var redeemPoints: Int?,
    @SerializedName("is_deleted")
    var isDeleted: Int?,
    @SerializedName("preparing_time")
    var preparingTime: String?,
    @SerializedName("dish_qty")
    var dishQty: String?,
    @SerializedName("food_type")
    var foodType: Int?,
    @SerializedName("category_id")
    var categoryId: Int?,
    @SerializedName("is_enable")
    var isEnable: Int?,
    @SerializedName("category_name")
    var categoryName: String?,
    @SerializedName("actual_price")
    var actualPrice: Int?
)