package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class PointHistoryResponse {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: PointHistoryData? = null
}

class PointHistoryData {
    @SerializedName("list")
    @Expose
    var list: List<PointHistoryList>? = null

    @SerializedName("restaurant_base_url")
    @Expose
    var restaurantBaseUrl: String? = null

    @SerializedName("total_points")
    @Expose
    var totalPoints: Int? = null
}

class PointHistoryList {
    @SerializedName("restaurant_title")
    @Expose
    var restaurantTitle: String? = null

    @SerializedName("points")
    @Expose
    var points: Int? = null

    @SerializedName("total")
    @Expose
    var total: String? = null

    @SerializedName("order_id")
    @Expose
    var orderId: Int? = null

    @SerializedName("get_point")
    @Expose
    var get_point: String? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("base_url")
    @Expose
    var baseUrl: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("time")
    @Expose
    var time: String? = null
}
