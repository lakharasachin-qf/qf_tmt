package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyBookingResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: MyBookingData? = null

}

class MyBookingData {
    @SerializedName("list")
    @Expose
    var list: List<MyBookingList>? = null

    @SerializedName("page")
    @Expose
    var page: Int? = null
}

class MyBookingList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("booking_date")
    @Expose
    var bookingDate: String? = null

    @SerializedName("booking_time")
    @Expose
    var bookingTime: String? = null

    @SerializedName("total_person")
    @Expose
    var totalPerson: Int? = null

    @SerializedName("adult")
    @Expose
    var adult: Int? = null

    @SerializedName("child")
    @Expose
    var child: Int? = null

    @SerializedName("special_request")
    @Expose
    var specialRequest: String? = null

    @SerializedName("occasion_id")
    @Expose
    var occasionId: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("table_id")
    @Expose
    var tableId: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("category_id")
    @Expose
    var category_id: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null

    @SerializedName("specialist_id")
    @Expose
    var specialistId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("base_url")
    @Expose
    var baseUrl: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("rating")
    @Expose
    var rating: String? = null

    @SerializedName("total_rating")
    @Expose
    var totalRating: String? = null
}
