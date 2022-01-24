package com.themarkettheory.user.newmodels.pointhistory


import com.google.gson.annotations.SerializedName

data class NewPointHistoryData(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("ingredients")
    var ingredients: String? = "",
    @SerializedName("unit")
    var unit: String? = "",
    @SerializedName("final_price")
    var finalPrice: String? = "",
    @SerializedName("point")
    var point: Int? = 0,
    @SerializedName("is_spicy")
    var isSpicy: Any,
    @SerializedName("is_recommonded")
    var isRecommonded: Any,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("tax")
    var tax: Any,
    @SerializedName("extra_topping")
    var extraTopping: Any,
    @SerializedName("user_customizable")
    var userCustomizable: Any,
    @SerializedName("size_data")
    var sizeData: String? = "",
    @SerializedName("custom_data")
    var customData: String? = "",
    @SerializedName("currency")
    var currency: String? = "",
    @SerializedName("redeem_points")
    var redeemPoints: Int? = 0,
    @SerializedName("is_deleted")
    var isDeleted: Any,
    @SerializedName("preparing_time")
    var preparingTime: String? = "",
    @SerializedName("get_point")
    var getPoint: String? = "",
    @SerializedName("dish_qty")
    var dishQty: String? = "",
    @SerializedName("food_type")
    var foodType: Any,
    @SerializedName("category_id")
    var categoryId: Int? = 0,
    @SerializedName("is_enable")
    var isEnable: Any,
    @SerializedName("category_name")
    var categoryName: String? = "",
    @SerializedName("actual_price")
    var actualPrice: Any,
    @SerializedName("date")
    var date: String? = "",
    @SerializedName("time")
    var time: String? = "",
    @SerializedName("qty")
    var qty: Int? = 0
)