package com.themarkettheory.user.newmodels.booking


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("adult")
    var adult: Int?,
    @SerializedName("booking_date")
    var bookingDate: String?,
    @SerializedName("booking_time")
    var bookingTime: String?,
    @SerializedName("child")
    var child: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("occasion_id")
    var occasionId: Int?,
    @SerializedName("occasion_name")
    var occasionName: String?,
    @SerializedName("order_id")
    var orderId: Int?,
    @SerializedName("service_id")
    var serviceId: Int?,
    @SerializedName("special_request")
    var specialRequest: String?,
    @SerializedName("specialist_id")
    var specialistId: Int?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("table_id")
    var tableId: Int?,
    @SerializedName("total_person")
    var totalPerson: Int?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("user_id")
    var userId: Int?
)