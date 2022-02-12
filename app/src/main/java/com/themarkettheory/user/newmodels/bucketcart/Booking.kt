package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("booking_date")
    var bookingDate: String?,
    @SerializedName("booking_time")
    var bookingTime: String?,
    @SerializedName("child")
    var child: Int?,
    @SerializedName("occasion_id")
    var occasionId: Int?,
    @SerializedName("occasion_name")
    var occasionName: String?,
    @SerializedName("order_id")
    var orderId: String?,
    @SerializedName("service_id")
    var serviceId: Int?,
    @SerializedName("special_request")
    var specialRequest: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("table_id")
    var tableId: Int?,
    @SerializedName("table_no")
    var table_no: String?,
    @SerializedName("total_person")
    var totalPerson: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("special_instruction")
    var specialInstruction: String?
)