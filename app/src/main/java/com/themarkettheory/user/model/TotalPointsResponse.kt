package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TotalPointsResponse : Serializable {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: TotalPointsData? = null
}

class TotalPointsData {
    val list: List<TotalPointsList>? = null

    val restaurantBaseUrl: String? = null

    val totalPoints: Int? = null

    class TotalPointsList {
        val restaurant_title: String? = null

        val points: Int? = null

        val total: String? = null

        val order_id: Int? = null

        val service_id: Int? = null

        val image: String? = null

        val base_url: String? = null
    }
}

class TotalPointsList {
    val restaurant_title: String? = null

    val points: Int? = null

    val total: String? = null

    val order_id: Int? = null

    val service_id: Int? = null

    val image: String? = null

    val base_url: String? = null
}


