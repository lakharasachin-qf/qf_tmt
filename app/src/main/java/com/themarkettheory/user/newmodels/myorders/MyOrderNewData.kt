package com.themarkettheory.user.newmodels.myorders

import com.google.gson.annotations.SerializedName

data class MyOrderNewData(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("booking_date")
    var bookingDate: String?,
    @SerializedName("booking_time")
    var bookingTime: String?,
    @SerializedName("child")
    var child: Int?,
    @SerializedName("adult")
    var adult: Int?,
    @SerializedName("occasion_name")
    var occasionName: String?,
    @SerializedName("occasion_id")
    var occasionId: Int?,
    @SerializedName("special_request")
    var specialRequest: String?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("table_id")
    var tableId: Int?,
    @SerializedName("service_details")
    var serviceDetails: ServiceDetails?,
    @SerializedName("order_details")
    var orderDetails: OrderDetails?,
    @SerializedName("total_person")
    var totalPerson: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("type")
    var type: String?
)
