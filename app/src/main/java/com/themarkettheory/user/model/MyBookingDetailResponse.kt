package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyBookingDetailResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: BookingDetailData? = null
}

class BookingDetailData {
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

    @SerializedName("is_reviewed")
    @Expose
    var is_reviewed: Int? = null

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

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("specialist_id")
    @Expose
    var specialistId: Int? = null

    @SerializedName("specialist")
    @Expose
    var specialist: Data? = null

    @SerializedName("services")
    @Expose
    var services: ArrayList<SalonSpaBookings>? = null

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

class SalonSpaBookings {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("order_id")
    @Expose
    var orderId: Int? = null

    @SerializedName("menu_id")
    @Expose
    var menuId: Int? = null

    @SerializedName("qty")
    @Expose
    var qty: Int? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("points")
    @Expose
    var points: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("is_redeem")
    @Expose
    var isRedeem: Int? = null

    @SerializedName("is_reviewed")
    @Expose
    var is_reviewed: Int? = null

    @SerializedName("size_id")
    @Expose
    var sizeId: Int? = null

    @SerializedName("saloon_services")
    @Expose
    var saloonServices: SaloonSpaList? = null
}