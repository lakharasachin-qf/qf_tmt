package com.themarkettheory.user.newmodels.orderconfirmation


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("user_id")
    var userId: Int?,
    @SerializedName("subtotal")
    var subtotal: String?,
    @SerializedName("discount_type")
    var discountType: String?,
    @SerializedName("discount")
    var discount: String?,
    @SerializedName("total")
    var total: String?,
    @SerializedName("offer_id")
    var offerId: Int?,
    @SerializedName("payment_id")
    var paymentId: String?,
    @SerializedName("tax")
    var tax: String?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("points")
    var points: Int?,
    @SerializedName("booking_id")
    var bookingId: Int?,
    @SerializedName("special_instruction")
    var specialInstruction: String?,
    @SerializedName("pickup_time")
    var pickupTime: String?,
    @SerializedName("table_no")
    var table_no: String?,
    @SerializedName("refund_id")
    var refundId: Any?,
    @SerializedName("reason")
    var reason: Any?,
    @SerializedName("order_type")
    var orderType: String?,
    @SerializedName("update_by")
    var updateBy: Int?,
    @SerializedName("date")
    var date: String?,
    @SerializedName("time")
    var time: String?,
    @SerializedName("order_number")
    var orderNumber: String?,
    @SerializedName("order_details")
    var orderDetails: OrderDetails?,
    @SerializedName("menu_details")
    var menuDetails: List<MenuDetail>?,
    @SerializedName("applied_coupons")
    var appliedCoupons: List<Any>?,
    @SerializedName("is_reviewed")
    var isReviewed: Int?,
    @SerializedName("service_details")
    var serviceDetails: ServiceDetails?
)