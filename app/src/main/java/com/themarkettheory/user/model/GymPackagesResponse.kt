package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GymPackagesResponse : Serializable {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: GymPackageData? = null
}

class GymPackageData : Serializable {
    @SerializedName("list")
    @Expose
    var list: List<GymList>? = null
}

class GymList : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("open_days")
    @Expose
    var openDays: List<OpenDays>? = null

    @SerializedName("open_time")
    @Expose
    var openTime: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null

    @SerializedName("duration")
    @Expose
    var duration: String? = null

    @SerializedName("discounted_price")
    @Expose
    var discountedPrice: String? = null

    @SerializedName("service_name")
    @Expose
    var service_name: String? = null

    @SerializedName("points")
    @Expose
    var points: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
}

class OpenDays : Serializable {
    @SerializedName("day")
    @Expose
    val day: String? = null

    @SerializedName("open_time")
    @Expose
    val openTime: String? = null

    @SerializedName("close_time")
    @Expose
    val closeTime: String? = null

}