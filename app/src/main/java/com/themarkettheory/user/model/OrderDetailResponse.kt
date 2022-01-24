package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderDetailResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: OrderDetailData? = null
}

class OrderDetailData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("type_id")
    @Expose
    var typeId: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("subtotal")
    @Expose
    var subtotal: String? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null

    @SerializedName("tax_amount")
    @Expose
    var taxAmount: String? = null

    @SerializedName("discount_type")
    @Expose
    var discountType: String? = null

    @SerializedName("discount")
    @Expose
    var discount: String? = null

    @SerializedName("tax")
    @Expose
    var tax: String? = null

    @SerializedName("total")
    @Expose
    var total: String? = null

    @SerializedName("ticket_number")
    @Expose
    var ticketNumber: String? = null

    @SerializedName("offer_id")
    @Expose
    var offerId: Int? = null

    @SerializedName("payment_id")
    @Expose
    var paymentId: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null

    @SerializedName("points")
    @Expose
    var points: Int? = null

    @SerializedName("special_instruction")
    @Expose
    var specialInstruction: String? = null

    @SerializedName("is_reviewed")
    @Expose
    var is_reviewed: Int? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("order_details")
    @Expose
    var orderDetails: List<OrderDetail>? = null

    @SerializedName("service_details")
    @Expose
    var service_details: ServiceDetail? = null

    @SerializedName("applied_coupons")
    @Expose
    var applied_coupons: ArrayList<OfferDatum>? = null
}

class OrderDetail {
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
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null

    @SerializedName("is_redeem")
    @Expose
    var isRedeem: Int? = null

    @SerializedName("size_id")
    @Expose
    var sizeId: Int? = null

    @SerializedName("menu")
    @Expose
    var menu: Menu? = null

    @SerializedName("cuisine")
    @Expose
    var cuisine: String? = null
}
