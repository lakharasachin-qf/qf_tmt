package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PointsDetailResponse {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: PointsDetailData? = null
}

class PointsDetailData {
    @SerializedName("list")
    @Expose
    var list: List<PointsList>? = null

    @SerializedName("restaurant_base_url")
    @Expose
    var restaurantBaseUrl: String? = null

    @SerializedName("total_points")
    @Expose
    var totalPoints: Int? = null

    @SerializedName("service_details")
    @Expose
    var service_details: ServiceDetail? = null
}

class PointsList {
    @SerializedName("menu_title")
    @Expose
    var menuTitle: String? = null

    @SerializedName("cuisines_name")
    @Expose
    var cuisinesName: String? = null

    @SerializedName("points")
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
}
